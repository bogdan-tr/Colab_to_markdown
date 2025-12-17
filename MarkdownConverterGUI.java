import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class MarkdownConverterGUI extends JFrame {
  private JTextArea inputArea;
  private JTextArea outputArea;
  private JComboBox<String> languageCombo;
  private JLabel fileNameLabel;
  private JButton convertButton;
  private JButton uploadButton;
  private JButton downloadButton;
  private JButton copyButton;
  private JButton clearButton;
  private File currentFile;

  private static final Color PRIMARY_BG = new Color(13, 0, 98);
  private static final Color SECONDARY_BG = new Color(0, 0, 0);
  private static final Color ACCENT = new Color(79, 150, 255);
  private static final Color ACCENT_HOVER = new Color(59, 130, 246);
  private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
  private static final Color TEXT_SECONDARY = new Color(203, 213, 225);
  private static final Color BORDER_COLOR = new Color(79, 150, 255, 120);
  private static final Color SUCCESS = new Color(34, 197, 94);
  private static final Color SUCCESS_HOVER = new Color(22, 163, 74);
  private static final Color DANGER = new Color(239, 68, 68);
  private static final Color DANGER_HOVER = new Color(220, 38, 38);
  private static final Color COPY_COLOR = new Color(168, 85, 247);
  private static final Color COPY_HOVER = new Color(147, 51, 234);

  public MarkdownConverterGUI() {
    setTitle("toMarkdown Converter");
    setSize(1400, 900);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    // Set app icon
    try {
      setIconImage(createIcon());
    } catch (Exception e) {
      System.err.println("Could not load icon: " + e.getMessage());
    }

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }

    initComponents();
    setVisible(true);
  }

  private Image createIcon() {
    // Create a 64x64 icon programmatically with sparkles
    int size = 64;
    java.awt.image.BufferedImage icon = new java.awt.image.BufferedImage(size, size,
        java.awt.image.BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = icon.createGraphics();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Draw gradient background circle
    GradientPaint gp = new GradientPaint(0, 0, new Color(0, 1, 74), size, size, new Color(77, 5, 146));
    g2d.setPaint(gp);
    g2d.fillOval(4, 4, size - 8, size - 8);

    // Draw "M" for Markdown in white
    g2d.setColor(Color.WHITE);
    g2d.setFont(new Font("Arial", Font.BOLD, 36));
    FontMetrics fm = g2d.getFontMetrics();
    String text = "M";
    int x = (size - fm.stringWidth(text)) / 2;
    int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
    g2d.drawString(text, x, y);

    // Draw small sparkle
    g2d.setColor(new Color(255, 255, 255, 200));
    g2d.fillOval(size - 18, 8, 6, 6);
    g2d.fillOval(8, size - 18, 6, 6);

    g2d.dispose();
    return icon;
  }

  private void initComponents() {
    JPanel mainPanel = new JPanel(new BorderLayout(20, 20)) {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();
        GradientPaint gp = new GradientPaint(
            0, 0, PRIMARY_BG,
            w, h, SECONDARY_BG);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
      }
    };
    mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

    mainPanel.add(createHeader(), BorderLayout.NORTH);
    mainPanel.add(createControlPanel(), BorderLayout.CENTER);
    mainPanel.add(createEditorPanel(), BorderLayout.SOUTH);

    add(mainPanel);
  }

  private JPanel createHeader() {
    JPanel headerPanel = new JPanel();
    headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
    headerPanel.setOpaque(false);

    JLabel titleLabel = new JLabel("✨ toMarkdown");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
    titleLabel.setForeground(TEXT_PRIMARY);
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel subtitleLabel = new JLabel(
        "Convert Python auto-generated from Jupyter Notebooks to beautiful Markdown documentation");
    subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
    subtitleLabel.setForeground(TEXT_SECONDARY);
    subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel authorLabel = new JLabel("by Bogdan Trigubov");
    authorLabel.setFont(new Font("Arial", Font.ITALIC, 12));
    authorLabel.setForeground(new Color(168, 85, 247));
    authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    headerPanel.add(titleLabel);
    headerPanel.add(Box.createVerticalStrut(10));
    headerPanel.add(subtitleLabel);
    headerPanel.add(Box.createVerticalStrut(5));
    headerPanel.add(authorLabel);

    return headerPanel;
  }

  private JPanel createControlPanel() {
    JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
    controlPanel.setBackground(new Color(20, 10, 60, 220));
    controlPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER_COLOR, 2, true),
        new EmptyBorder(15, 20, 15, 20)));

    uploadButton = createStyledButton("📁 Upload File", ACCENT, ACCENT_HOVER);
    uploadButton.addActionListener(e -> uploadFile());

    String[] languages = { "python", "r", "julia", "matlab", "go" };
    languageCombo = new JComboBox<>(languages);
    languageCombo.setFont(new Font("Arial", Font.PLAIN, 14));
    languageCombo.setBackground(new Color(10, 5, 40));
    languageCombo.setForeground(TEXT_PRIMARY);
    languageCombo.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 2, true));
    languageCombo.setPreferredSize(new Dimension(150, 40));

    fileNameLabel = new JLabel("No file selected");
    fileNameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    fileNameLabel.setForeground(TEXT_SECONDARY);
    fileNameLabel.setBorder(new EmptyBorder(0, 10, 0, 10));

    // Convert button with dynamic colors
    convertButton = new JButton("⚡ Convert");
    convertButton.setFont(new Font("Arial", Font.BOLD, 13));
    convertButton.setForeground(Color.WHITE);
    convertButton.setBackground(DANGER);
    convertButton.setFocusPainted(false);
    convertButton.setBorderPainted(false);
    convertButton.setPreferredSize(new Dimension(150, 40));
    convertButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    convertButton.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(DANGER, 3, true),
        new EmptyBorder(8, 15, 8, 15)));

    convertButton.addMouseListener(new MouseAdapter() {
      public void mouseEntered(MouseEvent e) {
        updateConvertButtonHover(true);
      }

      public void mouseExited(MouseEvent e) {
        updateConvertButtonHover(false);
      }
    });

    convertButton.addActionListener(e -> convertToMarkdown());

    copyButton = createStyledButton("📋 Copy", COPY_COLOR, COPY_HOVER);
    copyButton.addActionListener(e -> copyToClipboard());
    copyButton.setEnabled(false);

    downloadButton = createStyledButton("💾 Download", ACCENT, ACCENT_HOVER);
    downloadButton.addActionListener(e -> downloadFile());
    downloadButton.setEnabled(false);

    clearButton = createStyledButton("🗑️ Clear", DANGER, DANGER_HOVER);
    clearButton.addActionListener(e -> clearAll());

    controlPanel.add(uploadButton);
    controlPanel.add(languageCombo);
    controlPanel.add(fileNameLabel);
    controlPanel.add(convertButton);
    controlPanel.add(copyButton);
    controlPanel.add(downloadButton);
    controlPanel.add(clearButton);

    return controlPanel;
  }

  private void updateConvertButtonColor(boolean isHover) {
    boolean hasInput = inputArea != null && !inputArea.getText().trim().isEmpty();
    Color color;

    if (isHover) {
      color = hasInput ? SUCCESS_HOVER : DANGER_HOVER;
    } else {
      color = hasInput ? SUCCESS : DANGER;
    }

    convertButton.setBackground(color);
    convertButton.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(color, 3, true),
        new EmptyBorder(8, 15, 8, 15)));
  }

  private void updateConvertButtonHover(boolean isHover) {
    updateConvertButtonColor(isHover);
  }

  private JPanel createEditorPanel() {
    JPanel editorPanel = new JPanel(new GridLayout(1, 2, 20, 0));
    editorPanel.setOpaque(false);
    editorPanel.setPreferredSize(new Dimension(1360, 600));

    editorPanel.add(createTextPanel("📝 Input (.py file)", true));
    editorPanel.add(createTextPanel("📄 Output (Markdown)", false));

    return editorPanel;
  }

  private JPanel createTextPanel(String title, boolean isInput) {
    JPanel panel = new JPanel(new BorderLayout(0, 0));
    panel.setBackground(new Color(15, 10, 50, 220));
    panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 2, true));

    JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    headerPanel.setBackground(new Color(30, 20, 80, 150));
    headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

    JLabel headerLabel = new JLabel(title);
    headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
    headerLabel.setForeground(TEXT_PRIMARY);
    headerPanel.add(headerLabel);

    JTextArea textArea = new JTextArea();
    textArea.setFont(new Font("Consolas", Font.PLAIN, 13));
    textArea.setBackground(new Color(5, 5, 25, 240));
    textArea.setForeground(isInput ? new Color(147, 197, 253) : new Color(216, 180, 254));
    textArea.setCaretColor(TEXT_PRIMARY);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.setBorder(new EmptyBorder(10, 10, 10, 10));

    if (isInput) {
      inputArea = textArea;
      // Add document listener to update button color when typing
      inputArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
          updateConvertButtonColor(false);
        }

        public void removeUpdate(javax.swing.event.DocumentEvent e) {
          updateConvertButtonColor(false);
        }

        public void changedUpdate(javax.swing.event.DocumentEvent e) {
          updateConvertButtonColor(false);
        }
      });
    } else {
      textArea.setEditable(false);
      outputArea = textArea;
    }

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setBorder(null);
    scrollPane.getViewport().setBackground(new Color(5, 5, 25, 240));

    panel.add(headerPanel, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
  }

  private JButton createStyledButton(String text, Color bg, Color hoverBg) {
    JButton button = new JButton(text);
    button.setFont(new Font("Arial", Font.BOLD, 13));
    button.setForeground(Color.WHITE);
    button.setBackground(bg);
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setPreferredSize(new Dimension(150, 40));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));

    button.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(bg, 3, true),
        new EmptyBorder(8, 15, 8, 15)));

    button.addMouseListener(new MouseAdapter() {
      public void mouseEntered(MouseEvent e) {
        if (button.isEnabled()) {
          button.setBackground(hoverBg);
          button.setBorder(BorderFactory.createCompoundBorder(
              BorderFactory.createLineBorder(hoverBg, 3, true),
              new EmptyBorder(8, 15, 8, 15)));
        }
      }

      public void mouseExited(MouseEvent e) {
        if (button.isEnabled()) {
          button.setBackground(bg);
          button.setBorder(BorderFactory.createCompoundBorder(
              BorderFactory.createLineBorder(bg, 3, true),
              new EmptyBorder(8, 15, 8, 15)));
        }
      }
    });

    return button;
  }

  private void uploadFile() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new FileNameExtensionFilter("Python Files", "py", "txt"));

    int result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      currentFile = fileChooser.getSelectedFile();
      fileNameLabel.setText(currentFile.getName());
      fileNameLabel.setForeground(ACCENT);

      try {
        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          content.append(line).append("\n");
        }
        reader.close();
        inputArea.setText(content.toString());
      } catch (IOException e) {
        JOptionPane.showMessageDialog(this,
            "Error reading file: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void convertToMarkdown() {
    String input = inputArea.getText();
    if (input.trim().isEmpty()) {
      JOptionPane.showMessageDialog(this,
          "Please enter some text or upload a file first!",
          "No Input",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    try {
      File tempInput = File.createTempFile("tomarkdown_input", ".txt");
      BufferedWriter writer = new BufferedWriter(new FileWriter(tempInput));
      writer.write(input);
      writer.close();

      String language = (String) languageCombo.getSelectedItem();
      toMarkdown converter = new toMarkdown(tempInput.getAbsolutePath(), language);
      ArrayList<Integer> updateIndexList = converter.processIndex();
      converter.invert(updateIndexList);

      File tempOutput = File.createTempFile("tomarkdown_output", ".md");
      converter.write(tempOutput.getAbsolutePath());

      BufferedReader reader = new BufferedReader(new FileReader(tempOutput));
      StringBuilder result = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        result.append(line).append("\n");
      }
      reader.close();

      outputArea.setText(result.toString());
      copyButton.setEnabled(true);
      downloadButton.setEnabled(true);

      tempInput.delete();
      tempOutput.delete();

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this,
          "Error during conversion: " + e.getMessage(),
          "Conversion Error",
          JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
    }
  }

  private void copyToClipboard() {
    String text = outputArea.getText();
    if (text.trim().isEmpty()) {
      return;
    }

    StringSelection stringSelection = new StringSelection(text);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, null);

    JOptionPane.showMessageDialog(this,
        "Markdown copied to clipboard!",
        "Success",
        JOptionPane.INFORMATION_MESSAGE);
  }

  private void downloadFile() {
    if (outputArea.getText().trim().isEmpty()) {
      return;
    }

    JFileChooser fileChooser = new JFileChooser();
    String defaultName = currentFile != null ? currentFile.getName().replace(".py", ".md") : "output.md";
    fileChooser.setSelectedFile(new File(defaultName));

    int result = fileChooser.showSaveDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      File outputFile = fileChooser.getSelectedFile();

      try {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write(outputArea.getText());
        writer.close();

        JOptionPane.showMessageDialog(this,
            "File saved successfully!",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
      } catch (IOException e) {
        JOptionPane.showMessageDialog(this,
            "Error saving file: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void clearAll() {
    inputArea.setText("");
    outputArea.setText("");
    fileNameLabel.setText("No file selected");
    fileNameLabel.setForeground(TEXT_SECONDARY);
    currentFile = null;
    copyButton.setEnabled(false);
    downloadButton.setEnabled(false);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new MarkdownConverterGUI());
  }
}
