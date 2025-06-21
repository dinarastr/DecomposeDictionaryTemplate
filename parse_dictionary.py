import json
import csv
import os

def parse_dictionary_json_to_csv(json_file_path, csv_file_path):
    """
    Parse a dictionary JSON file and create a CSV with id, word, and entry columns.
    
    Args:
        json_file_path (str): Path to the input JSON file
        csv_file_path (str): Path to the output CSV file
    """
    try:
        # Read the JSON file
        with open(json_file_path, 'r', encoding='utf-8') as file:
            data = json.load(file)
        
        # Extract the line array from the document
        entries = data.get('document', {}).get('line', [])
        
        # Create CSV file
        with open(csv_file_path, 'w', newline='', encoding='utf-8') as csvfile:
            fieldnames = ['id', 'word', 'entry']
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
            
            # Write header
            writer.writeheader()
            
            # Process each entry
            for index, entry in enumerate(entries, start=1):
                # Extract the word
                word = entry.get('word', '')
                
                # Convert the entire entry to JSON string
                entry_json = json.dumps(entry, ensure_ascii=False, separators=(',', ':'))
                
                # Write row to CSV
                writer.writerow({
                    'id': index,
                    'word': word,
                    'entry': entry_json
                })
        
        print(f"Successfully created CSV file: {csv_file_path}")
        print(f"Processed {len(entries)} dictionary entries")
        
    except FileNotFoundError:
        print(f"Error: File '{json_file_path}' not found.")
    except json.JSONDecodeError as e:
        print(f"Error: Invalid JSON format - {e}")
    except Exception as e:
        print(f"Error: {e}")

def main():
    # File paths
    json_file = "shared/src/commonMain/composeResources/files/json (2).txt"
    csv_file = "dictionary_entries.csv"
    
    # Check if JSON file exists
    if not os.path.exists(json_file):
        print(f"JSON file not found at: {json_file}")
        print("Please make sure the file path is correct.")
        return
    
    # Parse JSON and create CSV
    parse_dictionary_json_to_csv(json_file, csv_file)
    
    # Display sample of the created CSV
    print("\nSample of created CSV:")
    try:
        with open(csv_file, 'r', encoding='utf-8') as file:
            reader = csv.reader(file)
            for i, row in enumerate(reader):
                if i < 5:  # Show first 5 rows
                    print(f"Row {i+1}: {row[0]}, {row[1]}, {row[2][:100]}..." if len(row[2]) > 100 else f"Row {i+1}: {row}")
                else:
                    break
    except Exception as e:
        print(f"Error reading CSV for preview: {e}")

if __name__ == "__main__":
    main() 