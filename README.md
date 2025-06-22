# 📊 JSON Tree Viewer for Android

An interactive, customizable JSON viewer for Android. Displays JSON data as an expandable tree, with editing, search, and export features — built entirely with native Android components and **no third-party libraries**.

---

## Link to app demo video
https://drive.google.com/file/d/1WwXc_uNJkD5ZLNCZ0u-15tyrIQTZr7Ea/view?usp=drive_link

## Link to full project zip file
https://drive.google.com/file/d/1EesCCreiBIc8bxjaa20bV5__4vHaqgyt/view?usp=sharing

## ✨ Features

- ✅ **Tree View Display**  
  Visualize JSON data as a collapsible/expandable tree structure.

- ✏️ **Node Editing**  
  - Edit values with a dialog  
  - Add child nodes  
  - Delete nodes

- 📋 **Copy/Share Tools**  
  - Copy entire JSON to clipboard  
  - Copy individual keys/values  
  - Share JSON via any app (e.g., email, WhatsApp)

- 🔍 **Search and Highlight**  
  - Real-time search through keys and values  
  - Matches are highlighted dynamically

- 📂 **Input Options**  
  - Load JSON from a text field  
  - Load from a remote URL  
  - Import from `.json` file using file picker

- 💾 **Export**  
  - Save updated JSON to device's Downloads folder

- 🔧 **JSON Validation (Planned)**  
  - Warning on invalid structures  
  - Highlight and explain malformed sections

- ⬇️⬆️ **(Planned)** Navigate between search matches  
- 🌳 **(Planned)** Expand/Collapse All nodes

---

## 🧱 Tech Stack

- Language: Java  
- Platform: Android  
- UI: XML layouts, Material Components  
- Minimum SDK: 21+

---

## 🚀 Getting Started

### 1. Clone the repo

```bash
git clone https://github.com/your-username/json-tree-viewer.git
```

### 2. Open in Android Studio

- File > Open > Select the project folder
- Sync Gradle

### 3. Run on emulator or device

---

## 🌈 Color Legend for JSON Value Types

Each value in the tree is displayed with a distinct color based on its type:

| Type      | Example            | Color Code | Description           |
|-----------|--------------------|------------|-----------------------|
| String    | "Hello world"      | #1976D2    | Blue for strings      |
| Number    | 42, 3.14           | #388E3C    | Green for numbers     |
| Boolean   | true, false        | #F57C00    | Orange for booleans   |
| Null      | null               | Gray       | Gray for null values  |
| Object/Array | {...}, [...]    | Black      | Default text color    |

---

## 📸 Screenshots

![image](https://github.com/user-attachments/assets/0a4d8ec4-7f73-4e40-8a28-37e8d79fa55d)
