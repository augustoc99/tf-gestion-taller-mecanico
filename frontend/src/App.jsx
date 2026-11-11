import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Navegacion from './componentes/Navegacion';
import PaginaFormularioCliente from './paginas/clientes/PaginaFormularioCliente';
import PaginaListaClientes from './paginas/clientes/PaginaListaClientes';
import PaginaCrearOrden from './paginas/ordenes/PaginaCrearOrden';
import PaginaDetalleOrden from './paginas/ordenes/PaginaDetalleOrden';
import PaginaListaOrdenes from './paginas/ordenes/PaginaListaOrdenes';

export default function App() {
  return (
    <BrowserRouter>
      <Navegacion />

      <Routes>
        <Route path="/" element={<PaginaListaOrdenes />} />

        <Route path="/ordenes" element={<PaginaListaOrdenes />} />
        <Route path="/ordenes/nueva" element={<PaginaCrearOrden />} />
        <Route path="/ordenes/:id" element={<PaginaDetalleOrden />} />

        <Route path="/clientes" element={<PaginaListaClientes />} />
        <Route path="/clientes/nuevo" element={<PaginaFormularioCliente />} />
        <Route path="/clientes/:id/editar" element={<PaginaFormularioCliente />} />
      </Routes>
    </BrowserRouter>
  );
}
