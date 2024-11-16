import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class HospitalManagementGUI {
    private static Connection conn;
    private static JFrame frame;
    private static JPanel panel;
    private static JTextField patientNameField, patientAgeField, patientAilmentField;
    private static JTextField doctorNameField, doctorSpecialtyField;
    private static JTable patientsTable, doctorsTable, appointmentsTable;

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_management", "root", "Raghav@26");
            createGUI();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createGUI() {
        frame = new JFrame("Hospital Management System");
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Patients", createPatientsPanel());
        tabbedPane.addTab("Doctors", createDoctorsPanel());
        tabbedPane.addTab("Appointments", createAppointmentsPanel());

        panel.add(tabbedPane, BorderLayout.CENTER);
        frame.add(panel);

        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static JPanel createPatientsPanel() {
        JPanel patientsPanel = new JPanel(new BorderLayout());

        // Table to view patients
        patientsTable = new JTable();
        loadPatientsData();
        JScrollPane scrollPane = new JScrollPane(patientsTable);
        patientsPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel to add new patient
        JPanel addPatientPanel = new JPanel(new GridLayout(4, 2));
        addPatientPanel.add(new JLabel("Patient Name:"));
        patientNameField = new JTextField();
        addPatientPanel.add(patientNameField);

        addPatientPanel.add(new JLabel("Age:"));
        patientAgeField = new JTextField();
        addPatientPanel.add(patientAgeField);

        addPatientPanel.add(new JLabel("Ailment:"));
        patientAilmentField = new JTextField();
        addPatientPanel.add(patientAilmentField);

        JButton addPatientButton = new JButton("Add Patient");
        addPatientButton.addActionListener(e -> addPatient());
        addPatientPanel.add(addPatientButton);

        patientsPanel.add(addPatientPanel, BorderLayout.SOUTH);
        return patientsPanel;
    }

    private static JPanel createDoctorsPanel() {
        JPanel doctorsPanel = new JPanel(new BorderLayout());

        // Table to view doctors
        doctorsTable = new JTable();
        loadDoctorsData();
        JScrollPane scrollPane = new JScrollPane(doctorsTable);
        doctorsPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel to add new doctor
        JPanel addDoctorPanel = new JPanel(new GridLayout(3, 2));
        addDoctorPanel.add(new JLabel("Doctor Name:"));
        doctorNameField = new JTextField();
        addDoctorPanel.add(doctorNameField);

        addDoctorPanel.add(new JLabel("Specialty:"));
        doctorSpecialtyField = new JTextField();
        addDoctorPanel.add(doctorSpecialtyField);

        JButton addDoctorButton = new JButton("Add Doctor");
        addDoctorButton.addActionListener(e -> addDoctor());
        addDoctorPanel.add(addDoctorButton);

        doctorsPanel.add(addDoctorPanel, BorderLayout.SOUTH);
        return doctorsPanel;
    }

    private static JPanel createAppointmentsPanel() {
        JPanel appointmentsPanel = new JPanel(new BorderLayout());

        // Table to view appointments
        appointmentsTable = new JTable();
        loadAppointmentsData();
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        appointmentsPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel to add new appointment
        JPanel addAppointmentPanel = new JPanel(new GridLayout(2, 2));
        addAppointmentPanel.add(new JLabel("Select Patient ID:"));
        JComboBox<Integer> patientComboBox = new JComboBox<>();
        loadPatientsForCombo(patientComboBox);
        addAppointmentPanel.add(patientComboBox);

        addAppointmentPanel.add(new JLabel("Select Doctor ID:"));
        JComboBox<Integer> doctorComboBox = new JComboBox<>();
        loadDoctorsForCombo(doctorComboBox);
        addAppointmentPanel.add(doctorComboBox);

        JButton addAppointmentButton = new JButton("Schedule Appointment");
        addAppointmentButton.addActionListener(e -> scheduleAppointment(patientComboBox, doctorComboBox));
        addAppointmentPanel.add(addAppointmentButton);

        appointmentsPanel.add(addAppointmentPanel, BorderLayout.SOUTH);
        return appointmentsPanel;
    }

    private static void loadPatientsData() {
        String query = "SELECT * FROM patients";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            patientsTable.setModel(buildTableModel(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loadDoctorsData() {
        String query = "SELECT * FROM doctors";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            doctorsTable.setModel(buildTableModel(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loadAppointmentsData() {
        String query = "SELECT a.id, p.name AS patient_name, d.name AS doctor_name FROM appointments a JOIN patients p ON a.patient_id = p.id JOIN doctors d ON a.doctor_id = d.id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            appointmentsTable.setModel(buildTableModel(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loadPatientsForCombo(JComboBox<Integer> comboBox) {
        String query = "SELECT id FROM patients";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                comboBox.addItem(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loadDoctorsForCombo(JComboBox<Integer> comboBox) {
        String query = "SELECT id FROM doctors";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                comboBox.addItem(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addPatient() {
        String name = patientNameField.getText();
        int age = Integer.parseInt(patientAgeField.getText());
        String ailment = patientAilmentField.getText();

        String query = "INSERT INTO patients (name, age, ailment) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.setString(3, ailment);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Patient added successfully!");
            loadPatientsData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addDoctor() {
        String name = doctorNameField.getText();
        String specialty = doctorSpecialtyField.getText();

        String query = "INSERT INTO doctors (name, specialty) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, specialty);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Doctor added successfully!");
            loadDoctorsData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void scheduleAppointment(JComboBox<Integer> patientComboBox, JComboBox<Integer> doctorComboBox) {
        int patientId = (int) patientComboBox.getSelectedItem();
        int doctorId = (int) doctorComboBox.getSelectedItem();

        String query = "INSERT INTO appointments (patient_id, doctor_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, patientId);
            stmt.setInt(2, doctorId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Appointment scheduled successfully!");
            loadAppointmentsData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static javax.swing.table.TableModel buildTableModel(ResultSet rs) throws SQLException {
        var metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        var columnNames = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnNames[i] = metaData.getColumnName(i + 1);
        }

        var data = new Object[100][columnCount]; // 100 rows max
        int rowCount = 0;
        while (rs.next()) {
            for (int i = 0; i < columnCount; i++) {
                data[rowCount][i] = rs.getObject(i + 1);
            }
            rowCount++;
        }

        return new javax.swing.table.DefaultTableModel(data, columnNames);
    }
}
