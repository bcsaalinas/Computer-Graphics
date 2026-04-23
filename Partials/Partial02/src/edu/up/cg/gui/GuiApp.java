package edu.up.cg.gui;

import edu.up.cg.pipeline.VideoPipeline;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

// Swing UI that lets the user pick media files, then runs VideoPipeline while showing progress
public class GuiApp {

    private JFrame frame;
    private JLabel selectionLabel;
    private JButton selectButton;
    private JButton generateButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private List<String> selectedFiles = new ArrayList<>();
    private String outputFolder;

    // builds and displays the main window
    public void show() {
        frame = new JFrame("Video Creator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(520, 260);
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(20, 20, 20, 20));

        // selection row — button on the left, selected count on the right
        JPanel selectionRow = new JPanel(new BorderLayout(10, 0));
        selectButton = new JButton("Select Media Files");
        selectionLabel = new JLabel("No files selected");
        selectionRow.add(selectButton, BorderLayout.WEST);
        selectionRow.add(selectionLabel, BorderLayout.CENTER);
        selectionRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        selectionRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        root.add(selectionRow);

        root.add(Box.createVerticalStrut(15));

        // generate button — disabled until at least one file has been selected
        generateButton = new JButton("Generate Video");
        generateButton.setEnabled(false);
        generateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        generateButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        root.add(generateButton);

        root.add(Box.createVerticalStrut(20));

        // progress bar shows the overall percentage
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        root.add(progressBar);

        root.add(Box.createVerticalStrut(8));

        // status label shows what the pipeline is doing right now
        statusLabel = new JLabel(" ");
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        root.add(statusLabel);

        frame.setContentPane(root);
        frame.setVisible(true);

        selectButton.addActionListener(e -> onSelectFiles());
        generateButton.addActionListener(e -> onGenerate());
    }

    // opens a multi-file chooser and stores the selected file paths
    private void onSelectFiles() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(true);
        chooser.setDialogTitle("Select media files");

        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File[] files = chooser.getSelectedFiles();
            selectedFiles.clear();
            for (File file : files) {
                selectedFiles.add(file.getAbsolutePath());
            }

            // output goes next to the first selected file
            outputFolder = files[0].getParentFile().getAbsolutePath();

            selectionLabel.setText(files.length + " file" + (files.length == 1 ? "" : "s") + " selected");
            generateButton.setEnabled(true);
        }
    }

    // runs the pipeline on a background thread so the UI stays responsive and the progress bar updates
    private void onGenerate() {
        selectButton.setEnabled(false);
        generateButton.setEnabled(false);
        progressBar.setValue(0);
        statusLabel.setText("Starting...");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                VideoPipeline pipeline = new VideoPipeline();
                return pipeline.run(selectedFiles, outputFolder, (percent, message) -> {
                    // listener is called from the worker thread, so bounce UI updates to the EDT
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setValue(percent);
                        statusLabel.setText(message);
                    });
                });
            }

            @Override
            protected void done() {
                try {
                    String videoPath = get();
                    statusLabel.setText("Done!");
                    JOptionPane.showMessageDialog(frame,
                            "Video saved to:\n" + videoPath,
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    statusLabel.setText("Error — see dialog for details");
                    JOptionPane.showMessageDialog(frame,
                            e.getCause() != null ? e.getCause().getMessage() : e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    selectButton.setEnabled(true);
                    generateButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}
