#!/bin/bash

# toMarkdown Installation Script
# Author: Bogdan Trigubov

set -e  # Exit on error

echo "=========================================="
echo "  toMarkdown Installer"
echo "  by Bogdan Trigubov"
echo "=========================================="
echo ""

# Check if Java is installed
if ! command -v javac &> /dev/null; then
    echo "❌ Error: Java JDK is not installed."
    echo "Please install Java JDK 11 or higher and try again."
    echo ""
    echo "Install Java:"
    echo "  macOS:   brew install openjdk@17"
    echo "  Ubuntu:  sudo apt install openjdk-17-jdk"
    echo "  Windows: Download from https://adoptium.net/"
    exit 1
fi

# Get Java version
JAVA_VERSION=$(javac -version 2>&1 | awk '{print $2}' | cut -d'.' -f1)
echo "✓ Java JDK found (version $JAVA_VERSION)"

# Determine installation directory
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    # Windows
    INSTALL_DIR="$HOME/bin/tomarkdown"
    BIN_DIR="$HOME/bin"
else
    # macOS/Linux
    INSTALL_DIR="$HOME/.local/share/tomarkdown"
    BIN_DIR="$HOME/.local/bin"
fi

echo "Installing to: $INSTALL_DIR"
echo ""

# Create directories
mkdir -p "$INSTALL_DIR"
mkdir -p "$BIN_DIR"

# Compile Java files
echo "📦 Compiling Java files..."
javac -d "$INSTALL_DIR" *.java

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed!"
    exit 1
fi

echo "✓ Compilation successful"

# Create launcher script for Unix-like systems
if [[ "$OSTYPE" != "msys" && "$OSTYPE" != "win32" ]]; then
    LAUNCHER="$BIN_DIR/tomd"
    
    cat > "$LAUNCHER" << 'EOF'
#!/bin/bash
# toMarkdown launcher script - launches GUI

INSTALL_DIR="$HOME/.local/share/tomarkdown"
java -cp "$INSTALL_DIR" toMd --gui
EOF
    
    chmod +x "$LAUNCHER"
    echo "✓ Created launcher script: $LAUNCHER"
fi

# Create Windows batch file
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    LAUNCHER="$BIN_DIR/tomd.bat"
    
    cat > "$LAUNCHER" << 'EOF'
@echo off
set INSTALL_DIR=%USERPROFILE%\bin\tomarkdown
java -cp "%INSTALL_DIR%" toMd --gui
EOF
    
    echo "✓ Created launcher script: $LAUNCHER"
fi

# Check if BIN_DIR is in PATH
if [[ ":$PATH:" != *":$BIN_DIR:"* ]]; then
    echo ""
    echo "⚠️  Warning: $BIN_DIR is not in your PATH"
    echo ""
    echo "Add this line to your shell configuration file:"
    
    if [[ "$SHELL" == *"zsh"* ]]; then
        echo "  echo 'export PATH=\"$BIN_DIR:\$PATH\"' >> ~/.zshrc"
        echo "  source ~/.zshrc"
    elif [[ "$SHELL" == *"bash"* ]]; then
        if [[ "$OSTYPE" == "darwin"* ]]; then
            echo "  echo 'export PATH=\"$BIN_DIR:\$PATH\"' >> ~/.bash_profile"
            echo "  source ~/.bash_profile"
        else
            echo "  echo 'export PATH=\"$BIN_DIR:\$PATH\"' >> ~/.bashrc"
            echo "  source ~/.bashrc"
        fi
    else
        echo "  export PATH=\"$BIN_DIR:\$PATH\""
    fi
    
    if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
        echo ""
        echo "Or add to Windows PATH:"
        echo "  setx PATH \"%PATH%;$BIN_DIR\""
    fi
else
    echo "✓ $BIN_DIR is already in PATH"
fi

echo ""
echo "=========================================="
echo "✓ Installation complete!"
echo "=========================================="
echo ""
echo "Usage:"
echo "  tomd                    Launch GUI"
echo ""
echo "If 'tomd' command not found, restart your terminal or run:"

if [[ "$SHELL" == *"zsh"* ]]; then
    echo "  source ~/.zshrc"
elif [[ "$SHELL" == *"bash"* ]]; then
    if [[ "$OSTYPE" == "darwin"* ]]; then
        echo "  source ~/.bash_profile"
    else
        echo "  source ~/.bashrc"
    fi
fi

echo ""
