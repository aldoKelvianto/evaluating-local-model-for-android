# Evaluating Local Model for Android Development

This project evaluates multiple local AI models for generating Kotlin code and code diffs for Android development tasks. It uses LangChain4j to connect to local models (via LM Studio) and validates the generated code using tree-sitter parsing.

## Overview

The project consists of two main components:

1. **Code Generation** (`notebooks/kotlin/code-generation.ipynb`): A Kotlin Jupyter notebook that generates code using multiple AI models
2. **Validation** (`run_validation.py`): A Python script that validates the generated code against predefined criteria

## Resources

- **Presentation**: [View presentation](https://docs.google.com/presentation/d/15Olek6cGvy34pL3Eu-saiL5uet1wiMyYw9rP8iXvGPs/edit?usp=sharing)
- **Result Data**: [View results spreadsheet](https://docs.google.com/spreadsheets/d/14hXsjP8VT2bECxpWwyk-ZZV0U6h8qg2n8zGaHlR1Mk8/edit?usp=sharing)

## Setup

### Prerequisites

- Python 3.x with pip
- Jupyter Notebook
- Kotlin Jupyter kernel
- LM Studio (or compatible local model server) running on `http://127.0.0.1:1234`
- Tree-sitter binaries (already included in `build/tree-sitter-binaries/`)

### Installation

```bash
pip install -r requirements.txt
```

## Code Generation

The `code-generation.ipynb` notebook generates code for 5 different tasks:

1. **test1-preview**: Generate Kotlin Preview composables
2. **test2-unit-test**: Generate unit tests
3. **test3-instrumentation-test**: Generate instrumentation tests
4. **test4-deprecated-material**: Generate diff for Material deprecation migration
5. **test5-deprecated-plugin**: Generate diff for plugin migration

### Models Evaluated

- `microsoft/phi-4`
- `openai/gpt-oss-20b`
- `mistralai/devstral-small-2-2512`
- `google/gemma-3-27b`
- `qwen/qwen3-coder-30b`

### Running the Notebook

1. Start LM Studio and load a model
2. Open `notebooks/kotlin/code-generation.ipynb` in Jupyter
3. Execute all cells sequentially

The notebook will:
- Connect to the local model server
- Generate code for each task using each model
- Monitor resource usage (RAM/VRAM)
- Save results to `build/` directories
- Generate execution metrics CSV files

### Output Structure

```
build/
├── test1-preview/
│   ├── result1-microsoft_phi-4.kt
│   ├── result2-openai_gpt-oss-20b.kt
│   ├── ...
│   └── execution-results.csv
├── test2-unit-test/
│   └── ...
└── ...
```

Each task directory contains:
- Generated code files (`.kt` or `.diff`)
- `execution-results.csv` with metrics (duration, token counts, RAM/VRAM usage)

## Validation

The `run_validation.py` script validates generated code using tree-sitter parsing to check:

- **Kotlin files**: Syntax correctness, required annotations, imports, function counts
- **Diff files**: Correct syntax, required deletions/additions

### Running Validation

```bash
python run_validation.py \
    --mappings resources/config/file-mappings-target.json \
    --output build/validation-results/results-target.csv
```

### Validation Criteria

- **test1-preview**: Valid syntax, `@Preview` and `@Composable` annotations
- **test2-unit-test**: Valid syntax, required imports, use case constructor, exactly 2 test functions
- **test3-instrumentation-test**: Valid syntax, TopicEntity import, DatabaseTest implementation, at least 5 test functions
- **test4-deprecated-material**: Valid diff syntax, required Material API changes
- **test5-deprecated-plugin**: Valid diff syntax, required plugin migration changes

The output CSV includes validation results for each model-task combination with success/failure details.

## Project Structure

```
.
├── notebooks/kotlin/
│   └── code-generation.ipynb    # Main code generation notebook
├── resources/
│   ├── prompts/                  # System prompts and task prompts
│   └── config/
│       └── file-mappings-target.json  # File mappings for validation
├── build/                        # Generated outputs
├── run_validation.py             # Validation script
└── requirements.txt              # Python dependencies
```
