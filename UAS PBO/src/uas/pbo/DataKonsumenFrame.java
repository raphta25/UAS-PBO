/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uas.pbo;

/**
 *
 * @author malir
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DataKonsumenFrame extends JFrame {
    private JTextField tfIdKonsumen, tfNamaKonsumen, tfAlamatKonsumen, tfTeleponKonsumen;
    private JTable tableKonsumen;
    private DefaultTableModel tableModel;

    public DataKonsumenFrame() {
        setTitle("CRUD Data Konsumen");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel utama
        JPanel panelMain = new JPanel(new BorderLayout(10, 10));

        // Panel input
        JPanel panelInput = createInputPanel();
        panelMain.add(panelInput, BorderLayout.WEST);

        // Tabel konsumen
        tableModel = new DefaultTableModel(new String[]{"ID Konsumen", "Nama Konsumen", "Alamat", "Telepon"}, 0);
        tableKonsumen = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableKonsumen);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Daftar Konsumen"));
        panelMain.add(scrollPane, BorderLayout.CENTER);

        add(panelMain);

        // Load data konsumen
        loadDataKonsumen();
    }

    private JPanel createInputPanel() {
        JPanel panelInput = new JPanel(new GridLayout(5, 2, 10, 10));
        panelInput.setBorder(BorderFactory.createTitledBorder("Form Input Data Konsumen"));

        tfIdKonsumen = new JTextField();
        tfNamaKonsumen = new JTextField();
        tfAlamatKonsumen = new JTextField();
        tfTeleponKonsumen = new JTextField();

        panelInput.add(new JLabel("ID Konsumen:"));
        panelInput.add(tfIdKonsumen);
        panelInput.add(new JLabel("Nama Konsumen:"));
        panelInput.add(tfNamaKonsumen);
        panelInput.add(new JLabel("Alamat:"));
        panelInput.add(tfAlamatKonsumen);
        panelInput.add(new JLabel("Telepon:"));
        panelInput.add(tfTeleponKonsumen);

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnTambah = new JButton("Tambah");
        JButton btnHapus = new JButton("Hapus");
        JButton btnResetForm = new JButton("Reset Form");
        JButton btnResetTabel = new JButton("Reset Tabel");
        JButton btnTampilkan = new JButton("Tampilkan Data");

        panelTombol.add(btnTambah);
        panelTombol.add(btnHapus);
        panelTombol.add(btnResetForm);
        panelTombol.add(btnResetTabel);
        panelTombol.add(btnTampilkan);

        btnTambah.addActionListener(this::tambahKonsumen);
        btnHapus.addActionListener(this::hapusKonsumen);
        btnResetForm.addActionListener(e -> resetForm());
        btnResetTabel.addActionListener(e -> resetTabel());
        btnTampilkan.addActionListener(e -> loadDataKonsumen());

        JPanel panelInputTombol = new JPanel(new BorderLayout());
        panelInputTombol.add(panelInput, BorderLayout.CENTER);
        panelInputTombol.add(panelTombol, BorderLayout.SOUTH);

        return panelInputTombol;
    }

    private void tambahKonsumen(ActionEvent e) {
        String idKonsumen = tfIdKonsumen.getText();
        String namaKonsumen = tfNamaKonsumen.getText();
        String alamatKonsumen = tfAlamatKonsumen.getText();
        String teleponKonsumen = tfTeleponKonsumen.getText();

        if (idKonsumen.isEmpty() || namaKonsumen.isEmpty() || alamatKonsumen.isEmpty() || teleponKonsumen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua kolom wajib diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO data_konsumen (id_konsumen, nama_konsumen, alamat, telepon) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, idKonsumen);
            stmt.setString(2, namaKonsumen);
            stmt.setString(3, alamatKonsumen);
            stmt.setString(4, teleponKonsumen);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data konsumen berhasil ditambahkan!", "Info", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadDataKonsumen();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menambahkan data konsumen!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusKonsumen(ActionEvent e) {
        int selectedRow = tableKonsumen.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String idKonsumen = tableModel.getValueAt(selectedRow, 0).toString();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM data_konsumen WHERE id_konsumen = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, idKonsumen);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data konsumen berhasil dihapus!", "Info", JOptionPane.INFORMATION_MESSAGE);
            loadDataKonsumen();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menghapus data konsumen!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        tfIdKonsumen.setText("");
        tfNamaKonsumen.setText("");
        tfAlamatKonsumen.setText("");
        tfTeleponKonsumen.setText("");
    }

    private void resetTabel() {
        tableModel.setRowCount(0);
    }

    private void loadDataKonsumen() {
        tableModel.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM data_konsumen ORDER BY id_konsumen ASC";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getString("id_konsumen"),
                    rs.getString("nama_konsumen"),
                    rs.getString("alamat"),
                    rs.getString("telepon")
                };
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data konsumen!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DataKonsumenFrame().setVisible(true);
        });
    }
}









