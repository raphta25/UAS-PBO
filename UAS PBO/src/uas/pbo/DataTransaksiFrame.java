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

public class DataTransaksiFrame extends JFrame {
    private JTextField tfIdTransaksi, tfIdKonsumen, tfIdBarang, tfJumlah, tfTanggal;
    private JTable tableTransaksi;
    private DefaultTableModel tableModel;

    public DataTransaksiFrame() {
        setTitle("CRUD Data Transaksi");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel utama
        JPanel panelMain = new JPanel(new BorderLayout(10, 10));

        // Panel input
        JPanel panelInput = createInputPanel();
        panelMain.add(panelInput, BorderLayout.WEST);

        // Tabel transaksi
        tableModel = new DefaultTableModel(new String[]{"ID Transaksi", "ID Konsumen", "ID Barang", "Jumlah", "Tanggal"}, 0);
        tableTransaksi = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableTransaksi);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Daftar Transaksi"));
        panelMain.add(scrollPane, BorderLayout.CENTER);

        add(panelMain);

        // Load data transaksi
        loadDataTransaksi();
    }

    private JPanel createInputPanel() {
        JPanel panelInput = new JPanel(new GridLayout(6, 2, 10, 10));
        panelInput.setBorder(BorderFactory.createTitledBorder("Form Input Data Transaksi"));

        tfIdTransaksi = new JTextField();
        tfIdKonsumen = new JTextField();
        tfIdBarang = new JTextField();
        tfJumlah = new JTextField();
        tfTanggal = new JTextField();

        panelInput.add(new JLabel("ID Transaksi:"));
        panelInput.add(tfIdTransaksi);
        panelInput.add(new JLabel("ID Konsumen:"));
        panelInput.add(tfIdKonsumen);
        panelInput.add(new JLabel("ID Barang:"));
        panelInput.add(tfIdBarang);
        panelInput.add(new JLabel("Jumlah:"));
        panelInput.add(tfJumlah);
        panelInput.add(new JLabel("Tanggal (YYYY-MM-DD):"));
        panelInput.add(tfTanggal);

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

        btnTambah.addActionListener(this::tambahTransaksi);
        btnHapus.addActionListener(this::hapusTransaksi);
        btnResetForm.addActionListener(e -> resetForm());
        btnResetTabel.addActionListener(e -> resetTabel());
        btnTampilkan.addActionListener(e -> loadDataTransaksi());

        JPanel panelInputTombol = new JPanel(new BorderLayout());
        panelInputTombol.add(panelInput, BorderLayout.CENTER);
        panelInputTombol.add(panelTombol, BorderLayout.SOUTH);

        return panelInputTombol;
    }

    private void tambahTransaksi(ActionEvent e) {
        String idTransaksi = tfIdTransaksi.getText();
        String idKonsumen = tfIdKonsumen.getText();
        String idBarang = tfIdBarang.getText();
        String jumlah = tfJumlah.getText();
        String tanggal = tfTanggal.getText();

        if (idTransaksi.isEmpty() || idKonsumen.isEmpty() || idBarang.isEmpty() || jumlah.isEmpty() || tanggal.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua kolom wajib diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO data_transaksi (id_transaksi, id_konsumen, id_barang, jumlah, tanggal) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, idTransaksi);
            stmt.setString(2, idKonsumen);
            stmt.setString(3, idBarang);
            stmt.setInt(4, Integer.parseInt(jumlah));
            stmt.setString(5, tanggal);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data transaksi berhasil ditambahkan!", "Info", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadDataTransaksi();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menambahkan data transaksi!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusTransaksi(ActionEvent e) {
        int selectedRow = tableTransaksi.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String idTransaksi = tableModel.getValueAt(selectedRow, 0).toString();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM data_transaksi WHERE id_transaksi = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, idTransaksi);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data transaksi berhasil dihapus!", "Info", JOptionPane.INFORMATION_MESSAGE);
            loadDataTransaksi();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menghapus data transaksi!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        tfIdTransaksi.setText("");
        tfIdKonsumen.setText("");
        tfIdBarang.setText("");
        tfJumlah.setText("");
        tfTanggal.setText("");
    }

    private void resetTabel() {
        tableModel.setRowCount(0);
    }

    private void loadDataTransaksi() {
        tableModel.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM data_transaksi ORDER BY id_transaksi ASC";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getString("id_transaksi"),
                    rs.getString("id_konsumen"),
                    rs.getString("id_barang"),
                    rs.getInt("jumlah"),
                    rs.getString("tanggal")
                };
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data transaksi!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DataTransaksiFrame().setVisible(true);
        });
    }
}


