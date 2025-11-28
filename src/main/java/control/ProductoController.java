package control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import modelo.Producto;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ProductoController implements Initializable {

    @FXML private TableView<Producto> tabla;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;

    @FXML private ListView<String> carrito;
    @FXML private TextField txtCant;

    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private final ObservableList<String> listaCarrito = FXCollections.observableArrayList();


    private final ArrayList<Producto> productosEnCarrito = new ArrayList<>();
    private final ArrayList<Integer> cantidadesEnCarrito = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));
        colPrecio.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrecio()).asObject());
        colStock.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getStock()).asObject());

        tabla.setItems(listaProductos);
        carrito.setItems(listaCarrito);

        cargarProductosDesdeDat();
    }

    private void cargarProductosDesdeDat() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("productos.dat"))) {
            ArrayList<Producto> temp = (ArrayList<Producto>) ois.readObject();
            listaProductos.addAll(temp);
        } catch (Exception e) {
            mostrarAlerta("Error al cargar el archivo productos.dat");
        }
    }

    @FXML
    private void agregarAlCarrito() {
        Producto seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Debe seleccionar un producto");
            return;
        }

        String textoCant = txtCant.getText().trim();
        if (textoCant.isEmpty()) {
            mostrarAlerta("Debe ingresar una cantidad");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(textoCant);
        } catch (NumberFormatException e) {
            mostrarAlerta("La cantidad debe ser un número válido");
            return;
        }

        if (cantidad <= 0) {
            mostrarAlerta("La cantidad debe ser mayor a 0");
            return;
        }
        if (cantidad > seleccionado.getStock()) {
            mostrarAlerta("No hay suficiente stock");
            return;
        }

        productosEnCarrito.add(seleccionado);
        cantidadesEnCarrito.add(cantidad);
        listaCarrito.add(seleccionado.getNombre() + " - " + cantidad);
        txtCant.clear();
    }

    @FXML
    private void confirmarCompra() {
        if (listaCarrito.isEmpty()) {
            mostrarAlerta("El carrito está vacío");
            return;
        }

        double total = 0.0;

        try (PrintWriter pw = new PrintWriter("ticket.txt")) {
            for (int i = 0; i < productosEnCarrito.size(); i++) {
                Producto p = productosEnCarrito.get(i);
                int cant = cantidadesEnCarrito.get(i);
                p.setStock(p.getStock() - cant);
                double subtotal = p.getPrecio() * cant;
                total += subtotal;
                pw.println("Producto: " + p.getNombre() + " Cantidad: " + cant + " Subtotal: " + subtotal);
            }
            pw.println("TOTAL A PAGAR: $" + total);
        } catch (Exception e) {
            mostrarAlerta("Error al generar el ticket");
        }

        tabla.refresh(); //


        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("productos.dat"))) {
            oos.writeObject(new ArrayList<>(listaProductos));
        } catch (Exception e) {
            mostrarAlerta("Error al guardar el archivo");
        }


        productosEnCarrito.clear();
        cantidadesEnCarrito.clear();
        listaCarrito.clear();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Atención");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}