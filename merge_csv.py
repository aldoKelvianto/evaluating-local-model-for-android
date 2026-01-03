import csv
from pathlib import Path

BUILD_DIR = Path("build")
OUTPUT_FILE = BUILD_DIR / "merged-execution-results.csv"

csv_files = []

# Find all execution-results.csv under test* directories
for path in BUILD_DIR.glob("test*/execution-results.csv"):
    csv_files.append(path)

if not csv_files:
    raise FileNotFoundError("No execution-results.csv files found.")

merged_rows = []
header = None

for csv_file in csv_files:
    test_name = csv_file.parent.name

    with csv_file.open(newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)

        if header is None:
            header = ["test_name"] + reader.fieldnames

        for row in reader:
            row_with_test = {"test_name": test_name, **row}
            merged_rows.append(row_with_test)

# Write merged CSV
with OUTPUT_FILE.open("w", newline="", encoding="utf-8") as f:
    writer = csv.DictWriter(f, fieldnames=header)
    writer.writeheader()
    writer.writerows(merged_rows)

print(f"Merged {len(csv_files)} CSV files into {OUTPUT_FILE}")
