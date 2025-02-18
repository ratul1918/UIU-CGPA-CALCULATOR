import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;

public class CGPACalculator {
    private JFrame frame;
    private JButton addCourseButton, addRetakeButton, calculateButton, resetButton;
    private JTextField completedCreditsField, currentCGPAField;
    private ArrayList<JComboBox<Integer>> creditDropdowns = new ArrayList<>();
    private ArrayList<JComboBox<String>> gradeDropdowns = new ArrayList<>();
    private ArrayList<JComboBox<Integer>> retakeCreditDropdowns = new ArrayList<>();
    private ArrayList<JComboBox<String>> oldGradeDropdowns = new ArrayList<>();
    private ArrayList<JComboBox<String>> newGradeDropdowns = new ArrayList<>();
    private JPanel coursesPanel;
    private static final HashMap<String, Double> gradePoints = new HashMap<>();

    static {
        gradePoints.put("A", 4.00);
        gradePoints.put("A-", 3.67);
        gradePoints.put("B+", 3.33);
        gradePoints.put("B", 3.00);
        gradePoints.put("B-", 2.67);
        gradePoints.put("C+", 2.33);
        gradePoints.put("C", 2.00);
        gradePoints.put("C-", 1.67);
        gradePoints.put("D+", 1.33);
        gradePoints.put("D", 1.00);
        gradePoints.put("F", 0.00);
    }

    public CGPACalculator() {
        frame = new JFrame("UIU CGPA Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 800);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        completedCreditsField = new JTextField();
        currentCGPAField = new JTextField();
        topPanel.add(new JLabel("Completed Credits:"));
        topPanel.add(completedCreditsField);
        topPanel.add(new JLabel("Current CGPA:"));
        topPanel.add(currentCGPAField);
        frame.add(topPanel, BorderLayout.NORTH);

        coursesPanel = new JPanel();
        coursesPanel.setLayout(new BoxLayout(coursesPanel, BoxLayout.Y_AXIS));
        addCourseRow();
        addCourseRow();
        frame.add(new JScrollPane(coursesPanel), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        addCourseButton = new JButton("ADD MORE");
        addRetakeButton = new JButton("ADD RETAKE");
        calculateButton = new JButton("CALCULATE");
        resetButton = new JButton("RESET");

        addCourseButton.addActionListener(e -> addCourseRow());
        addRetakeButton.addActionListener(e -> addRetakeRow());
        calculateButton.addActionListener(e -> calculateCGPA());
        resetButton.addActionListener(e -> resetFields());

        bottomPanel.add(addCourseButton);
        bottomPanel.add(addRetakeButton);
        bottomPanel.add(calculateButton);
        bottomPanel.add(resetButton);

        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void addCourseRow() {
        JPanel rowPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JComboBox<Integer> creditDropdown = new JComboBox<>(new Integer[]{1, 2, 3, 4});
        JComboBox<String> gradeDropdown = new JComboBox<>(gradePoints.keySet().toArray(new String[0]));
        creditDropdowns.add(creditDropdown);
        gradeDropdowns.add(gradeDropdown);
        rowPanel.add(new JLabel("Course"));
        rowPanel.add(creditDropdown);
        rowPanel.add(gradeDropdown);
        coursesPanel.add(rowPanel);
        coursesPanel.revalidate();
    }

    private void addRetakeRow() {
        JPanel rowPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        JComboBox<Integer> creditDropdown = new JComboBox<>(new Integer[]{1, 2, 3, 4});
        JComboBox<String> oldGradeDropdown = new JComboBox<>(gradePoints.keySet().toArray(new String[0]));
        JComboBox<String> newGradeDropdown = new JComboBox<>(gradePoints.keySet().toArray(new String[0]));
        retakeCreditDropdowns.add(creditDropdown);
        oldGradeDropdowns.add(oldGradeDropdown);
        newGradeDropdowns.add(newGradeDropdown);
        rowPanel.add(new JLabel("Retake Course"));
        rowPanel.add(creditDropdown);
        rowPanel.add(oldGradeDropdown);
        rowPanel.add(newGradeDropdown);
        coursesPanel.add(rowPanel);
        coursesPanel.revalidate();
    }

    private void calculateCGPA() {
        double oldCredits = parseDouble(completedCreditsField.getText());
        double oldCGPA = parseDouble(currentCGPAField.getText());
        double newCredits = 0, totalPoints = 0;

        for (int i = 0; i < creditDropdowns.size(); i++) {
            int credit = (int) creditDropdowns.get(i).getSelectedItem();
            String grade = (String) gradeDropdowns.get(i).getSelectedItem();
            newCredits += credit;
            totalPoints += credit * gradePoints.get(grade);
        }

        for (int i = 0; i < retakeCreditDropdowns.size(); i++) {
            int credit = (int) retakeCreditDropdowns.get(i).getSelectedItem();
            String oldGrade = (String) oldGradeDropdowns.get(i).getSelectedItem();
            String newGrade = (String) newGradeDropdowns.get(i).getSelectedItem();
            totalPoints -= credit * gradePoints.get(oldGrade); // Remove old grade contribution
            totalPoints += credit * gradePoints.get(newGrade); // Add new grade contribution
        }

        double newGPA = (newCredits > 0) ? (totalPoints / newCredits) : 0;
        double totalCredits = oldCredits + newCredits;
        double overallCGPA = (totalCredits > 0) ? ((oldCredits * oldCGPA) + (newCredits * newGPA)) / totalCredits : 0;
        JOptionPane.showMessageDialog(frame, "Overall CGPA: " + String.format("%.2f", overallCGPA));
    }

    private void resetFields() {
        completedCreditsField.setText("");
        currentCGPAField.setText("");
        coursesPanel.removeAll();
        creditDropdowns.clear();
        gradeDropdowns.clear();
        retakeCreditDropdowns.clear();
        oldGradeDropdowns.clear();
        newGradeDropdowns.clear();
        addCourseRow();
        addCourseRow();
        coursesPanel.revalidate();
    }

    private double parseDouble(String text) {
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CGPACalculator::new);
    }
}