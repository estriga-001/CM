import fitz

pdf_path = "../ENIDH_CM_Tutorial4_2026.pdf"
doc = fitz.open(pdf_path)
print(f"Number of pages: {len(doc)}")

with open("scratch_tutorial_full.txt", "w", encoding="utf-8") as f:
    for i, page in enumerate(doc):
        f.write(f"\n--- PAGE {i+1} ---\n")
        f.write(page.get_text())

print("Extraction completed successfully!")
