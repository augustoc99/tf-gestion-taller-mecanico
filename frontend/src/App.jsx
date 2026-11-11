import { BrowserRouter, Routes, Route } from 'react-router-dom';
import PaginaListaOrdenes from './paginas/ordenes/PaginaListaOrdenes';
import PaginaDetalleOrden from './paginas/ordenes/PaginaDetalleOrden';
import PaginaCrearOrden from './paginas/ordenes/PaginaCrearOrden';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<PaginaListaOrdenes />} />
        <Route path="/ordenes" element={<PaginaListaOrdenes />} />
        <Route path="/ordenes/nueva" element={<PaginaCrearOrden />} />
        <Route path="/ordenes/:id" element={<PaginaDetalleOrden />} />
      </Routes>
    </BrowserRouter>
  );
}