# 📊 JSON Tree Viewer for Android

An interactive, customizable JSON viewer for Android. Displays JSON data as an expandable tree, with editing, search, and export features — built entirely with native Android components and **no third-party libraries**.

---

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

## 📁 Folder Structure

```
├── com.example.jsontreeview
│   ├── JsonViewerView.java        # Main JSON Viewer component
│   ├── JsonNode.java              # Data model
│   ├── JsonNodeAdapter.java       # RecyclerView Adapter
│   ├── JsonParserUtil.java        # JSON <-> Node conversion
│   └── TreeItemDecoration.java    # Optional item decoration
├── res/
│   ├── layout/
│   ├── drawable/
│   └── values/
└── MainActivity.java              # Demo usage
```

---

## 📸 Screenshots

> Coming soon…

---

## 📜 License

This project is open-source and free to use under the MIT License.
