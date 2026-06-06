import sys
for lib in ["pypdf", "PyPDF2", "pdfplumber", "fitz", "pdfminer"]:
    try:
        __import__(lib)
        print(f"FOUND: {lib} is installed!")
    except ImportError:
        print(f"NOT FOUND: {lib} is NOT installed.")
