// Import necessary packages
import javax.swing.*; // For GUI components like JFrame, JButton, etc.
import java.awt.*; // For layout managers and basic GUI tools
import java.awt.event.*; // For handling events like button clicks
import java.util.ArrayList; // For dynamic arrays
import java.util.HashMap; // For key-value pairs to store grade points

public class CGPACalculator {
    // Declare GUI components and data structures
    private JFrame frame;
    private JButton addCourseButton, addRetakeButton, calculateButton, resetButton;
    private JTextField completedCreditsField, currentCGPAField;

    // Lists to store dropdowns for course and retake info
    private ArrayList<JComboBox<Integer>> creditDropdowns = new ArrayList<>();
    private ArrayList<JComboBox<String>> gradeDropdowns = new ArrayList<>();
    private ArrayList<JComboBox<Integer>> retakeCreditDropdowns = new ArrayList<>();
    private ArrayList<JComboBox<String>> oldGradeDropdowns = new ArrayList<>();
    private ArrayList<JComboBox<String>> newGradeDropdowns = new ArrayList<>();

    private JPanel coursesPanel; // Panel to hold course input rows

    // Static map of grades and their corresponding grade points
    private static final HashMap<String, Double> gradePoints = new HashMap<>();

    // Initialize grade points
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

    // Constructor to set up the UI
    public CGPACalculator() {
        // Create main window
        frame = new JFrame("UIU CGPA Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 800);
        frame.setLayout(new BorderLayout());

        // Top panel for completed credits and current CGPA input
        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        completedCreditsField = new JTextField();
        currentCGPAField = new JTextField();
        topPanel.add(new JLabel("Completed Credits:"));
        topPanel.add(completedCreditsField);
        topPanel.add(new JLabel("Current CGPA:"));
        topPanel.add(currentCGPAField);
        frame.add(topPanel, BorderLayout.NORTH);

        // Panel to hold dynamically added course/retake input rows
        coursesPanel = new JPanel();
        coursesPanel.setLayout(new BoxLayout(coursesPanel, BoxLayout.Y_AXIS));
        addCourseRow(); // Add first course row
        addCourseRow(); // Add second course row
        frame.add(new JScrollPane(coursesPanel), BorderLayout.CENTER); // Add scroll

        // Bottom panel with buttons
        JPanel bottomPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        addCourseButton = new JButton("ADD MORE");
        addRetakeButton = new JButton("ADD RETAKE");
        calculateButton = new JButton("CALCULATE");
        resetButton = new JButton("RESET");

        // Add actions to buttons
        addCourseButton.addActionListener(e -> addCourseRow());
        addRetakeButton.addActionListener(e -> addRetakeRow());
        calculateButton.addActionListener(e -> calculateCGPA());
        resetButton.addActionListener(e -> resetFields());

        // Add buttons to bottom panel
        bottomPanel.add(addCourseButton);
        bottomPanel.add(addRetakeButton);
        bottomPanel.add(calculateButton);
        bottomPanel.add(resetButton);

        // Add bottom panel to the frame
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setVisible(true); // Show the window
    }

    // Adds a new row to enter course info (credit and grade)
    private void addCourseRow() {
        JPanel rowPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JComboBox<Integer> creditDropdown = new JComboBox<>(new Integer[]{1, 2, 3, 4}); // Credit options
        JComboBox<String> gradeDropdown = new JComboBox<>(gradePoints.keySet().toArray(new String[0])); // Grade options

        // Store dropdowns for later use
        creditDropdowns.add(creditDropdown);
        gradeDropdowns.add(gradeDropdown);

        // Add labels and dropdowns to the row
        rowPanel.add(new JLabel("Course"));
        rowPanel.add(creditDropdown);
        rowPanel.add(gradeDropdown);

        // Add row to courses panel
        coursesPanel.add(rowPanel);
        coursesPanel.revalidate(); // Refresh panel layout
    }

    // Adds a new row to enter retake course info (credit, old grade, new grade)
    private void addRetakeRow() {
        JPanel rowPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        JComboBox<Integer> creditDropdown = new JComboBox<>(new Integer[]{1, 2, 3, 4});
        JComboBox<String> oldGradeDropdown = new JComboBox<>(gradePoints.keySet().toArray(new String[0]));
        JComboBox<String> newGradeDropdown = new JComboBox<>(gradePoints.keySet().toArray(new String[0]));

        // Store retake dropdowns
        retakeCreditDropdowns.add(creditDropdown);
        oldGradeDropdowns.add(oldGradeDropdown);
        newGradeDropdowns.add(newGradeDropdown);

        // Add label and dropdowns to the row
        rowPanel.add(new JLabel("Retake Course"));
        rowPanel.add(creditDropdown);
        rowPanel.add(oldGradeDropdown);
        rowPanel.add(newGradeDropdown);

        // Add row to panel
        coursesPanel.add(rowPanel);
        coursesPanel.revalidate();
    }

    // Calculate the new CGPA including regular and retake courses
    private void calculateCGPA() {
        // Read current completed credits and CGPA
        double oldCredits = parseDouble(completedCreditsField.getText());
        double oldCGPA = parseDouble(currentCGPAField.getText());
        double newCredits = 0, totalPoints = 0;

        // Calculate points from newly added regular courses
        for (int i = 0; i < creditDropdowns.size(); i++) {
            int credit = (int) creditDropdowns.get(i).getSelectedItem();
            String grade = (String) gradeDropdowns.get(i).getSelectedItem();
            newCredits += credit; // Add to new credit total
            totalPoints += credit * gradePoints.get(grade); // Add weighted grade point
        }

        // Handle retake courses
        for (int i = 0; i < retakeCreditDropdowns.size(); i++) {
            int credit = (int) retakeCreditDropdowns.get(i).getSelectedItem();
            String oldGrade = (String) oldGradeDropdowns.get(i).getSelectedItem();
            String newGrade = (String) newGradeDropdowns.get(i).getSelectedItem();

            // Subtract old grade's contribution
            totalPoints -= credit * gradePoints.get(oldGrade);
            // Add new grade's contribution
            totalPoints += credit * gradePoints.get(newGrade);
        }

        // Calculate GPA for the new courses
        double newGPA = (newCredits > 0) ? (totalPoints / newCredits) : 0;

        // Combine old and new results to calculate overall CGPA
        double totalCredits = oldCredits + newCredits;
        double overallCGPA = (totalCredits > 0)
            ? ((oldCredits * oldCGPA) + (newCredits * newGPA)) / totalCredits
            : 0;

        // Show result in dialog box
        JOptionPane.showMessageDialog(frame, "Overall CGPA: " + String.format("%.2f", overallCGPA));
    }

    // Clears all fields and resets to default state
    private void resetFields() {
        completedCreditsField.setText(""); // Clear completed credits input
        currentCGPAField.setText(""); // Clear current CGPA input

        // Remove all course/retake rows
        coursesPanel.removeAll();

        // Clear all dropdown tracking lists
        creditDropdowns.clear();
        gradeDropdowns.clear();
        retakeCreditDropdowns.clear();
        oldGradeDropdowns.clear();
        newGradeDropdowns.clear();

        // Add default two course rows
        addCourseRow();
        addCourseRow();

        // Refresh layout
        coursesPanel.revalidate();
    }

    // Safely parse string to double, return 0 if invalid input
    private double parseDouble(String text) {
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // Main method to run the program
    public static void main(String[] args) {
        // Launch GUI in the Event Dispatch Thread (safe GUI practice)
        SwingUtilities.invokeLater(CGPACalculator::new);
    }
}
