from pathlib import Path
from tree_sitter import Language, Parser
import csv
import argparse
import json
import os

def validate_preview_generation(kt_bytes, parser, KOTLIN_LANGUAGE):
    """Validate Kotlin code for Preview generation task."""
    tree = parser.parse(kt_bytes)
    root_node = tree.root_node

    correct_syntax = not root_node.has_error

    query = KOTLIN_LANGUAGE.query("""
        ; Pattern 1: Standard declarations
        (function_declaration (simple_identifier) @func_name)

        ; Pattern 2: Expression-style (infix 'fun')
        (infix_expression
            (simple_identifier) @keyword_fun
            (#eq? @keyword_fun "fun")
            (call_expression (simple_identifier) @func_name)
        )
    """)

    all_functions = []

    for node, capture_name in query.captures(root_node):
        if capture_name == "func_name":
            func_name = node.text.decode('utf8')
            annotations = []

            anchor = node
            while anchor and anchor.type not in ('function_declaration', 'infix_expression'):
                anchor = anchor.parent

            if anchor:
                for child in anchor.children:
                    if child.type == 'modifiers':
                        for mod in child.children:
                            if mod.type == 'annotation':
                                annotations.append(mod.text.decode('utf8'))

                curr = anchor.parent
                while curr and curr.type in ('prefix_expression', 'annotated_lambda'):
                    for child in curr.children:
                        if child.type == 'annotation':
                            ann_text = child.text.decode('utf8')
                            if ann_text not in annotations:
                                annotations.append(ann_text)
                    curr = curr.parent

                all_functions.append({
                    'name': func_name,
                    'annotations': annotations
                })

    total_preview_composable_functions = sum(
        1 for func in all_functions
        if any('@Preview' in ann for ann in func['annotations']) and
        any('@Composable' in ann for ann in func['annotations'])
    )
    has_preview_composable = total_preview_composable_functions > 0

    validations = {
        'correct_syntax': correct_syntax,
        'has_preview_and_composable': has_preview_composable
    }

    return validations


def validate_unit_test_generation(kt_bytes, parser, KOTLIN_LANGUAGE):
    """Validate Kotlin code for Unit Test generation task."""
    tree = parser.parse(kt_bytes)
    root_node = tree.root_node

    correct_syntax = not root_node.has_error

    query = KOTLIN_LANGUAGE.query("""
        ; 1. Capture all import paths
        (import_header (identifier) @import_path)

        ; 2. Capture function names and their surrounding annotations
        (function_declaration (simple_identifier) @func_name)
        (infix_expression
            (simple_identifier) @kw_fun (#eq? @kw_fun "fun")
            (call_expression (simple_identifier) @func_name))

        ; 3. Capture constructor calls in properties (e.g., val x = ClassName())
        (property_declaration
            (variable_declaration (simple_identifier) @prop_name)
            (call_expression (simple_identifier) @constructor_name))
    """)

    results = {
        "imports": [],
        "functions": [],
        "constructors": []
    }

    for node, capture_name in query.captures(root_node):
        text = node.text.decode('utf8')

        if capture_name == "import_path":
            results["imports"].append(text)

        elif capture_name == "func_name":
            annotations = []
            anchor = node
            while anchor and anchor.type not in ('function_declaration', 'infix_expression'):
                anchor = anchor.parent

            if anchor:
                for child in anchor.children:
                    if child.type == 'modifiers':
                        for mod in child.children:
                            if mod.type == 'annotation':
                                annotations.append(mod.text.decode('utf8'))

                curr = anchor.parent
                while curr and curr.type in ('prefix_expression', 'annotated_lambda'):
                    for child in curr.children:
                        if child.type == 'annotation':
                            ann_text = child.text.decode('utf8')
                            if ann_text not in annotations:
                                annotations.append(ann_text)
                    curr = curr.parent

            results["functions"].append({"name": text, "annotations": annotations})

        elif capture_name == "constructor_name":
            results["constructors"].append(text)

    required_imports = [
        "com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic",
        "com.google.samples.apps.nowinandroid.core.model.data.Topic",
    ]
    has_all_required_imports = all(req_import in results['imports'] for req_import in required_imports)

    has_use_case_constructor = "GetFollowableTopicsUseCase" in results['constructors']

    test_functions_count = sum(
        1 for f in results['functions']
        if any("@Test" in a for a in f['annotations'])
    )
    has_exactly_two_test_functions = (test_functions_count == 2)

    validations = {
        'correct_syntax': correct_syntax,
        'has_all_required_imports': has_all_required_imports,
        'has_use_case_constructor': has_use_case_constructor,
        'has_exactly_two_test_functions': has_exactly_two_test_functions
    }

    return validations


def validate_instrumentation_test_generation(kt_bytes, parser, KOTLIN_LANGUAGE):
    """Validate Kotlin code for Instrumentation Test generation task."""
    tree = parser.parse(kt_bytes)
    root_node = tree.root_node

    correct_syntax = not root_node.has_error

    query = KOTLIN_LANGUAGE.query("""
        (import_header (identifier) @import_path)

        (function_declaration (simple_identifier) @func_name)
        (infix_expression
            (simple_identifier) @kw_fun (#eq? @kw_fun "fun")
            (call_expression (simple_identifier) @func_name))

        (class_declaration
            (type_identifier) @class_name
            (delegation_specifier
                (constructor_invocation
                    (user_type
                        (type_identifier) @parent_class)))?
            (class_body)?)
    """)

    results = {
        "imports": [],
        "functions": [],
        "classes": {}
    }

    for node, capture_name in query.captures(root_node):
        text = node.text.decode('utf8')

        if capture_name == "import_path":
            results["imports"].append(text)

        elif capture_name == "func_name":
            annotations = []
            anchor = node
            while anchor and anchor.type not in ('function_declaration', 'infix_expression'):
                anchor = anchor.parent

            if anchor:
                for child in anchor.children:
                    if child.type == 'modifiers':
                        for mod in child.children:
                            if mod.type == 'annotation':
                                annotations.append(mod.text.decode('utf8'))
                curr = anchor.parent
                while curr and curr.type in ('prefix_expression', 'annotated_lambda'):
                    for child in curr.children:
                        if child.type == 'annotation':
                            if (ann := child.text.decode('utf8')) not in annotations:
                                annotations.append(ann)
                    curr = curr.parent

            results["functions"].append({"name": text, "annotations": annotations})

        elif capture_name == "class_name":
            if text not in results["classes"]:
                results["classes"][text] = {"parents": []}

        elif capture_name == "parent_class":
            # Find which class this parent belongs to by traversing up
            curr = node
            while curr:
                if curr.type == 'class_declaration':
                    for child in curr.children:
                        if child.type == 'type_identifier':
                            class_name = child.text.decode('utf8')
                            if class_name in results["classes"]:
                                results["classes"][class_name]["parents"].append(text)
                            break
                    break
                curr = curr.parent

    results["classes"] = [{"name": k, "parents": v["parents"]} for k, v in results["classes"].items()]

    required_import = "com.google.samples.apps.nowinandroid.core.database.model.TopicEntity"
    has_topic_entity_import = required_import in results["imports"]

    implements_database_test = any("DatabaseTest" in c["parents"] for c in results["classes"])

    test_functions_count = sum(
        1 for f in results['functions']
        if any("@Test" in a for a in f['annotations'])
    )
    has_at_least_5_tests = (test_functions_count >= 5)

    validations = {
        'correct_syntax': correct_syntax,
        'has_topic_entity_import': has_topic_entity_import,
        'implements_database_test': implements_database_test,
        'has_at_least_5_tests': has_at_least_5_tests
    }

    return validations


def validate_material_diff(diff_text, parser, query):
    """Validate diff for Material deprecation task."""
    tree = parser.parse(bytes(diff_text, 'utf-8'))
    root_node = tree.root_node

    correct_syntax = not root_node.has_error

    captures = query.captures(root_node)

    deletions = []
    additions = []

    for node, capture_name in captures:
        text = node.text.decode('utf-8')
        # Remove - or + character and leading & trailing whitespaces
        content = text[1:].strip() if len(text) > 1 else ""
        if capture_name == 'deletion':
            deletions.append(content)
        elif capture_name == 'addition':
            additions.append(content)

    required_deletions = [
        "colors = TopAppBarDefaults.centerAlignedTopAppBarColors(",
        "colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),",
    ]
    required_additions = [
        "colors = TopAppBarDefaults.topAppBarColors(",
        "colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),",
    ]

    contains_all_deletions = all(
        any(req in deletion for deletion in deletions)
        for req in required_deletions
    )

    contains_all_additions = all(
        any(req in addition for addition in additions)
        for req in required_additions
    )

    validations = {
        'correct_syntax': correct_syntax,
        'contains_all_deletions': contains_all_deletions,
        'contains_all_additions': contains_all_additions
    }

    return validations


def validate_plugin_diff(diff_text, parser, query):
    """Validate diff for Plugin migration task."""
    tree = parser.parse(bytes(diff_text, 'utf-8'))
    root_node = tree.root_node

    correct_syntax = not root_node.has_error

    captures = query.captures(root_node)

    deletions = []
    additions = []

    for node, capture_name in captures:
        text = node.text.decode('utf-8')
        # Remove - or + character and leading & trailing whitespaces
        content = text[1:].strip() if len(text) > 1 else ""
        if capture_name == 'deletion':
            deletions.append(content)
        elif capture_name == 'addition':
            additions.append(content)

    required_deletions = [
        "import org.jetbrains.kotlin.gradle.dsl.KotlinTopLevelExtension",
        "private inline fun <reified T : KotlinTopLevelExtension> Project.configureKotlin() = configure<T> {",
    ]
    required_additions = [
        "import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension",
        "private inline fun <reified T : KotlinBaseExtension> Project.configureKotlin() = configure<T> {"
    ]

    contains_all_deletions = all(
        any(req in deletion for deletion in deletions)
        for req in required_deletions
    )

    contains_all_additions = all(
        any(req in addition for addition in additions)
        for req in required_additions
    )

    validations = {
        'correct_syntax': correct_syntax,
        'contains_all_deletions': contains_all_deletions,
        'contains_all_additions': contains_all_additions
    }

    return validations


def process_diff_file(diff_text, task, diff_parser, diff_query):
    """
    Process a single diff file and return validations.

    Args:
        diff_text: The diff file content as string
        task: Either 'material_diff' or 'plugin_diff'
        diff_parser: Tree-sitter parser instance for diff files
        diff_query: Tree-sitter query for diff parsing

    Returns:
        Dictionary of validations
    """
    if task == 'test4-deprecated-material':
        return validate_material_diff(diff_text, diff_parser, diff_query)
    elif task == 'test5-deprecated-plugin':
        return validate_plugin_diff(diff_text, diff_parser, diff_query)
    else:
        raise ValueError(f"Unknown diff task: {task}")


def process_kotlin_file(kt_bytes, task, kotlin_parser, KOTLIN_LANGUAGE):
    """
    Process a single Kotlin file and return validations.

    Args:
        kt_bytes: The Kotlin file content as bytes
        task: One of 'preview_generation', 'unit_test_generation', 'instrumentation_test_generation'
        kotlin_parser: Tree-sitter parser instance for Kotlin
        KOTLIN_LANGUAGE: Kotlin language instance

    Returns:
        Dictionary of validations
    """
    if task == 'test1-preview':
        return validate_preview_generation(kt_bytes, kotlin_parser, KOTLIN_LANGUAGE)
    elif task == 'test2-unit-test':
        return validate_unit_test_generation(kt_bytes, kotlin_parser, KOTLIN_LANGUAGE)
    elif task == 'test3-instrumentation-test':
        return validate_instrumentation_test_generation(kt_bytes, kotlin_parser, KOTLIN_LANGUAGE)
    else:
        raise ValueError(f"Unknown Kotlin task: {task}")


def process_all_files(file_mappings, kotlin_parser, KOTLIN_LANGUAGE, diff_parser=None, diff_query=None,
                      output_csv='validation_results.csv'):
    """
    Process multiple Kotlin files and diffs, generate CSV report.

    Args:
        file_mappings: List of dicts with keys: 'model_name', 'task', 'file_path'
                      task should be one of: 'preview_generation', 'unit_test_generation',
                      'instrumentation_test_generation', 'material_diff', 'plugin_diff'
        kotlin_parser: Tree-sitter parser instance for Kotlin
        KOTLIN_LANGUAGE: Kotlin language instance
        diff_parser: Tree-sitter parser instance for diff files (required for diff tasks)
        diff_query: Tree-sitter query for diff parsing (required for diff tasks)
        output_csv: Output CSV file path
    """

    results = []

    kotlin_tasks = ['test1-preview', 'test1-unit-test', 'test3-instrumentation_test']
    diff_tasks = ['test4-deprecated-material', 'test5-deprecated-plugin']

    for mapping in file_mappings:
        model_name = mapping['model_name']
        task = mapping['task']
        file_path = Path(mapping['file_path'])

        print(f"Processing {model_name} - {task}...")

        try:
            if task in diff_tasks:
                if diff_parser is None or diff_query is None:
                    raise ValueError("diff_parser and diff_query are required for diff tasks")

                diff_text = file_path.read_text()
                validations = process_diff_file(diff_text, task, diff_parser, diff_query)

            elif task in kotlin_tasks:
                kt_bytes = file_path.read_bytes()
                validations = process_kotlin_file(kt_bytes, task, kotlin_parser, KOTLIN_LANGUAGE)

            else:
                print(f"Unknown task: {task}")
                continue

            success_validations = [k for k, v in validations.items() if v]
            failed_validations = [k for k, v in validations.items() if not v]

            is_valid = len(failed_validations) == 0

            results.append({
                'model_name': model_name,
                'task': task,
                'is_valid': is_valid,
                'success_validation_count': len(success_validations),
                'failed_validation_count': len(failed_validations),
                'success_validation_list': ', '.join(success_validations) if success_validations else 'None',
                'failed_validation_list': ', '.join(failed_validations) if failed_validations else 'None'
            })

        except Exception as e:
            print(f"Error processing {file_path}: {e}")
            results.append({
                'model_name': model_name,
                'task': task,
                'is_valid': False,
                'success_validation_count': 0,
                'failed_validation_count': 0,
                'success_validation_list': 'None',
                'failed_validation_list': f'Error: {str(e)}'
            })

    os.makedirs(os.path.dirname(output_csv), exist_ok=True)

    with open(output_csv, 'w', newline='', encoding='utf-8') as csvfile:
        fieldnames = ['model_name', 'task', 'is_valid', 'success_validation_count',
                      'failed_validation_count', 'success_validation_list', 'failed_validation_list']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)

        writer.writeheader()
        for result in results:
            writer.writerow(result)

    print(f"\nResults written to {output_csv}")
    return results


def load_file_mappings_from_json(json_path):
    """Load file mappings from a JSON file."""
    with open(json_path, 'r') as f:
        return json.load(f)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description='Validate Kotlin files and diffs against predefined criteria'
    )
    parser.add_argument(
        '--mappings',
        type=str,
        required=True,
        help='Path to JSON file containing file mappings'
    )
    parser.add_argument(
        '--output',
        type=str,
        default='validation_results.csv',
        help='Output CSV file path (default: validation_results.csv)'
    )

    args = parser.parse_args()

    file_mappings = load_file_mappings_from_json(args.mappings)

    kotlin_parser = Parser()
    KOTLIN_LANGUAGE = Language('build/tree-sitter-binaries/kotlin.so', 'kotlin')
    kotlin_parser.set_language(KOTLIN_LANGUAGE)

    diff_parser = Parser()
    DIFF_LANGUAGE = Language('build/tree-sitter-binaries/diff.so', 'diff')
    diff_parser.set_language(DIFF_LANGUAGE)
    diff_query = DIFF_LANGUAGE.query("""
        (deletion) @deletion
        (addition) @addition
    """)

    results = process_all_files(
        file_mappings,
        kotlin_parser,
        KOTLIN_LANGUAGE,
        diff_parser,
        diff_query,
        args.output
    )

    for result in results:
        print(f"{result['model_name']} - {result['task']}: Valid={result['is_valid']}")
        print(f"  Success ({result['success_validation_count']}): {result['success_validation_list']}")
        print(f"  Failed ({result['failed_validation_count']}): {result['failed_validation_list']}\n")