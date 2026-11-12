import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../../api/axios';
import './clientes.css';

function obtenerMensajeError(error) {
  const data = error?.response?.data;

  if (typeof data === 'string' && data.trim()) {
    return data;
  }

  if (data?.detalle) {
    return data.detalle;
  }

  if (data?.message) {
    return data.message;
  }

  if (data?.error && typeof data.error === 'string') {
    return data.error;
  }

  return 'No se pudo completar la operación.';
}

export default function PaginaListaClientes() {
  const [clientes, setClientes] = useState([]);
  const [buscar, setBuscar] = useState('');
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');

  const cargarClientes = useCallback(async (criterio = '') => {
    setCargando(true);
    setError('');

    try {
      const response = await api.get('/clientes', {
        params: criterio.trim() ? { buscar: criterio.trim() } : {},
      });
      setClientes(response.data);
    } catch (err) {
      setError(obtenerMensajeError(err));
    } finally {
      setCargando(false);
    }
  }, []);

  useEffect(() => {
    cargarClientes();
  }, [cargarClientes]);

  function manejarBusqueda(event) {
    event.preventDefault();
    cargarClientes(buscar);
  }

  function limpiarBusqueda() {
    setBuscar('');
    cargarClientes('');
  }

  return (
    <main className="pagina-clientes">
      <div className="cabecera-modulo">
        <div>
          <h1>Clientes</h1>
          <p>Alta, consulta y modificación de clientes del taller.</p>
        </div>

        <Link className="boton-enlace" to="/clientes/nuevo">
          + Nuevo cliente
        </Link>
      </div>

      <form className="buscador-clientes" onSubmit={manejarBusqueda}>
        <label htmlFor="buscar-cliente">Buscar por nombre o DNI/CUIT</label>

        <div className="fila-busqueda">
          <input
            id="buscar-cliente"
            type="search"
            value={buscar}
            onChange={(event) => setBuscar(event.target.value)}
            placeholder="Ej.: Juan Perez o 30111222"
          />

          <button type="submit">Buscar</button>
          <button type="button" onClick={limpiarBusqueda}>
            Limpiar
          </button>
        </div>
      </form>

      {error && <p className="mensaje-error">{error}</p>}

      {cargando ? (
        <p>Cargando clientes...</p>
      ) : clientes.length === 0 ? (
        <p>No se encontraron clientes.</p>
      ) : (
        <div className="tabla-responsive">
          <table className="tabla-clientes">
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>DNI/CUIT</th>
                <th>Teléfono</th>
                <th>Email</th>
                <th>Dirección</th>
                <th></th>
              </tr>
            </thead>

            <tbody>
              {clientes.map((cliente) => (
                <tr key={cliente.idCliente}>
                  <td>{cliente.idCliente}</td>
                  <td>{cliente.nombre}</td>
                  <td>{cliente.dniCuit || '—'}</td>
                  <td>{cliente.telefono || '—'}</td>
                  <td>{cliente.email || '—'}</td>
                  <td>{cliente.direccion || '—'}</td>
                  <td>
                    <Link to={`/clientes/${cliente.idCliente}/editar`}>
                      Editar
                    </Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </main>
  );
}
