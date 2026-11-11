import { BrowserRouter, Routes, Route } from 'react-router-dom';
import PaginaListaOrdenes from './paginas/ordenes/PaginaListaOrdenes';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<PaginaListaOrdenes />} />
        <Route path="/ordenes" element={<PaginaListaOrdenes />} />
      </Routes>
    </BrowserRouter>
  );
}