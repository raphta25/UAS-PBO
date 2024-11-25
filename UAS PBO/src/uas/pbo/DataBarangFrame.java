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

public class DataBarangFrame extends JFrame {
    private JTextField tfIdBarang, tfNamaBarang, tfHargaBarang, tfStokBarang;
    private JTable tableBarang;
    private DefaultTableModel tableModel;

    public DataBarangFrame() {
        setTitle("CRUD Data Barang");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel utama yang menggabungkan input dan tabel
        JPanel panelMain = new JPanel(new BorderLayout(10, 10));

        // Panel input (untuk form input data)
        JPanel panelInput = createInputPanel();
        panelMain.add(panelInput, BorderLayout.WEST);  // Panel input di sebelah kiri

        // Tabel barang
        tableModel = new DefaultTableModel(new String[]{"ID Barang", "Nama Barang", "Harga", "Stok"}, 0);
        tableBarang = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableBarang);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Daftar Barang"));
        panelMain.add(scrollPane, BorderLayout.CENTER);  // Tabel barang di tengah

        add(panelMain);

        // Load data barang ke tabel
        loadDataBarang();
    }

    private JPanel createInputPanel() {
        // Panel untuk input data barang
        JPanel panelInput = new JPanel(new GridLayout(5, 2, 10, 10));
        panelInput.setBorder(BorderFactory.createTitledBorder("Form Input Data Barang"));

        // Input fields
        tfIdBarang = new JTextField();
        tfNamaBarang = new JTextField();
        tfHargaBarang = new JTextField();
        tfStokBarang = new JTextField();

        // Tambahkan label dan input field ke panel
        panelInput.add(new JLabel("ID Barang:"));
        panelInput.add(tfIdBarang);

        panelInput.add(new JLabel("Nama Barang:"));
        panelInput.add(tfNamaBarang);

        panelInput.add(new JLabel("Harga:"));
        panelInput.add(tfHargaBarang);

        panelInput.add(new JLabel("Stok:"));
        panelInput.add(tfStokBarang);

        // Panel tombol untuk menambahkan data dan reset
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

        // Tambahkan listener ke tombol
        btnTambah.addActionListener(this::tambahBarang);
        btnHapus.addActionListener(this::hapusBarang);
        btnResetForm.addActionListener(e -> resetForm());
        btnResetTabel.addActionListener(e -> resetTabel());
        btnTampilkan.addActionListener(e -> loadDataBarang());

        // Gabungkan form input dan panel tombol
        JPanel panelInputTombol = new JPanel(new BorderLayout());
        panelInputTombol.add(panelInput, BorderLayout.CENTER);
        panelInputTombol.add(panelTombol, BorderLayout.SOUTH);

        return panelInputTombol;
    }

    private void tambahBarang(ActionEvent e) {
        String idBarang = tfIdBarang.getText();
        String namaBarang = tfNamaBarang.getText();
        String hargaBarang = tfHargaBarang.getText();
        String stokBarang = tfStokBarang.getText();

        if (idBarang.isEmpty() || namaBarang.isEmpty() || hargaBarang.isEmpty() || stokBarang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua kolom wajib diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO data_barang (id_barang, nama_barang, harga, stok) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, idBarang);
            stmt.setString(2, namaBarang);
            stmt.setDouble(3, Double.parseDouble(hargaBarang));
            stmt.setInt(4, Integer.parseInt(stokBarang));

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data barang berhasil ditambahkan!", "Info", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadDataBarang();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menambahkan data barang!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusBarang(ActionEvent e) {
        int selectedRow = tableBarang.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String idBarang = tableModel.getValueAt(selectedRow, 0).toString();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM data_barang WHERE id_barang = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, idBarang);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data barang berhasil dihapus!", "Info", JOptionPane.INFORMATION_MESSAGE);
            loadDataBarang();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menghapus data barang!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        tfIdBarang.setText("");
        tfNamaBarang.setText("");
        tfHargaBarang.setText("");
        tfStokBarang.setText("");
    }

    private void resetTabel() {
        tableModel.setRowCount(0);
    }

    private void loadDataBarang() {
        // Hapus semua baris di tabel
        tableModel.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Query dengan ORDER BY untuk mengurutkan data berdasarkan ID Barang secara ascending
            String query = "SELECT * FROM data_barang ORDER BY id_barang ASC";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getString("id_barang"),   // ID Barang
                    rs.getString("nama_barang"), // Nama Barang
                    rs.getDouble("harga"),       // Harga
                    rs.getInt("stok")            // Stok
                };
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data barang!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DataBarangFrame().setVisible(true);
        });
    }
}




