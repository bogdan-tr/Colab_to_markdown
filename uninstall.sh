#!/bin/bash

# toMarkdown Uninstall Script
# Author: Bogdan Trigubov

echo "=========================================="
echo "  toMarkdown Uninstaller"
echo "=========================================="
echo ""

# Determine installation directories
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    # Windows
    INSTALL_DIR="$HOME/bin/tomarkdown"
    BIN_DIR="$HOME/bin"
    LAUNCHER="$BIN_DIR/tomd.bat"
else
    # macOS/Linux
    INSTALL_DIR="$HOME/.local/share/tomarkdown"
    BIN_DIR="$HOME/.local/bin"
    LAUNCHER="$BIN_DIR/tomd"
fi

# Remove installation directory
if [ -d "$INSTALL_DIR" ]; then
    echo "Removing installation directory: $INSTALL_DIR"
    rm -rf "$INSTALL_DIR"
    echo "✓ Installation directory removed"
else
    echo "⚠️  Installation directory not found: $INSTALL_DIR"
fi

# Remove launcher script
if [ -f "$LAUNCHER" ]; then
    echo "Removing launcher script: $LAUNCHER"
    rm -f "$LAUNCHER"
    echo "✓ Launcher script removed"
else
    echo "⚠️  Launcher script not found: $LAUNCHER"
fi

echo ""
echo "=========================================="
echo "✓ Uninstallation complete!"
echo "=========================================="
echo ""
echo "Note: You may want to manually remove the PATH entry from your shell config:"

if [[ "$SHELL" == *"zsh"* ]]; then
    echo "  ~/.zshrc"
elif [[ "$SHELL" == *"bash"* ]]; then
    if [[ "$OSTYPE" == "darwin"* ]]; then
        echo "  ~/.bash_profile"
    else
        echo "  ~/.bashrc"
    fi
fi

echo ""
