package mx.uam.ayd.proyecto.negocio.modelo;

import negocio.modelos.Cliente;
import negocio.modelos.PedidoDomicilio;
import negocio.modelos.Repartidor;
import datos.repositorios.PedidoDomicilioRepository;
import datos.repositorios.RepartidorRepository;
import java.util.List;

public class ServicioPedidoDomicilio {

    // Instanciamos los repositorios de la capa de datos
    private PedidoDomicilioRepository pedidoRepo = new PedidoDomicilioRepository();
    private RepartidorRepository repartidorRepo = new RepartidorRepository();

    /**
     * 1. Valida que los campos obligatorios del cliente no estén vacíos
     */
    public boolean validarDatosCliente(Cliente cliente) {
        if (cliente == null) return false;
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) return false;
        if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) return false;
        if (cliente.getDireccion() == null || cliente.getDireccion().trim().isEmpty()) return false;
        if (cliente.getNumeroCasa() == null || cliente.getNumeroCasa().trim().isEmpty()) return false;
        if (cliente.getEntreCalles() == null || cliente.getEntreCalles().trim().isEmpty()) return false;
        if (cliente.getColorCasa() == null || cliente.getColorCasa().trim().isEmpty()) return false;
        return true;
    }

    /**
     * 2. Calcula el subtotal sumando los precios de los productos seleccionados
     */
    public double calcularSubtotal(List<Object> productos) {
        double subtotal = 0.0;
        // Aquí iterarías sobre tu lista de productos para sumar sus precios reales
        // Ejemplo genérico:
        // for (Object prod : productos) { subtotal += prod.getPrecio(); }
        return subtotal;
    }

    /**
     * 3. Calcula el costo de envío según la dirección o zona
     */
    public double calcularCostoEnvio(String direccion) {
        // Regla de negocio: costo base de envío a domicilio
        return 45.00; 
    }

    /**
     * 4. Calcula el total final sumando subtotal y costo de envío
     */
    public double calcularTotalFinal(double subtotal, double costoEnvio) {
        return subtotal + costoEnvio;
    }

    /**
     * 5. Crea el objeto pedido, calcula importes y solicita guardarlo en datos
     */
    public PedidoDomicilio crearPedido(Cliente cliente, List<Object> productos, double subtotal, double costoEnvio) {
        double totalFinal = calcularTotalFinal(subtotal, costoEnvio);
        
        PedidoDomicilio pedido = new PedidoDomicilio();
        pedido.setCliente(cliente);
        pedido.setSubtotal(subtotal);
        pedido.setCostoEnvio(costoEnvio);
        pedido.setTotalFinal(totalFinal);
        pedido.setEstado("Registrado");

        // Guardar en la capa de datos
        pedidoRepo.guardarPedido(pedido);
        return pedido;
    }

    /**
     * 6. Obtiene la lista de repartidores con estatus disponible
     */
    public List<Repartidor> obtenerRepartidoresDisponibles() {
        return repartidorRepo.buscarDisponibles();
    }

    /**
     * 7. Calcula un tiempo estimado de espera (ej. en minutos) si no hay repartidores o según la fila
     */
    public int calcularTiempoEstimadoEspera() {
        // Lógica de estimación (ej. 30-45 minutos base)
        return 35; 
    }

    /**
     * 8. Asigna un repartidor al pedido y actualiza su estado a "En camino"
     */
    public void asignarRepartidor(int idPedido, int idRepartidor) {
        PedidoDomicilio pedido = pedidoRepo.buscarPorId(idPedido);
        if (pedido != null) {
            // Aquí buscarías el objeto repartidor por ID y se lo asignas
            Repartidor repartidor = new Repartidor(); 
            repartidor.setIdRepartidor(idRepartidor);
            
            pedido.setRepartidorAsignado(repartidor);
            pedido.setEstado("En camino");
            
            pedidoRepo.guardarPedido(pedido); // Actualiza el pedido
        }
    }

    /**
     * 9. Cambia el estado del pedido (ej. a "Entregado" para cerrar la venta)
     */
    public void cambiarEstado(int idPedido, String nuevoEstado) {
        PedidoDomicilio pedido = pedidoRepo.buscarPorId(idPedido);
        if (pedido != null) {
            pedido.setEstado(nuevoEstado);
            pedidoRepo.guardarPedido(pedido);
        }
    }
}