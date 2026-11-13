import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../../api/axios';
import './vehiculos.css';

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

export default function PaginaListaVehiculos() {
  const [vehiculos, setVehiculos] = useState([]);
  const [clientes, setClientes] = useState([]);
  const [buscar, setBuscar] = useState('');
  const [idCliente, setIdCliente] = useState('');
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');

  const cargarVehiculos = useCallback(async (criterio = '', cliente = '') => {
    setCargando(true);
    setError('');

    try {
      const params = {};

      if (criterio.trim()) {
        params.buscar = criterio.trim();
      }

      if (cliente) {
        params.idCliente = cliente;
      }

      const response = await api.get('/vehiculos', { params });
      setVehiculos(response.data);
    } catch (err) {
      setError(obtenerMensajeError(err));
    } finally {
      setCargando(false);
    }
  }, []);

  useEffect(() => {
    cargarVehiculos();

    api.get('/clientes')
      .then((response) => setClientes(response.data))
      .catch((err) => setError(obtenerMensajeError(err)));
  }, [cargarVehiculos]);

  function manejarBusqueda(event) {
    event.preventDefault();
    cargarVehiculos(buscar, idCliente);
  }

  function limpiarFiltros() {
    setBuscar('');
    setIdCliente('');
    cargarVehiculos('', '');
  }

  return (
    <main className="pagina-vehiculos">
      <div className="cabecera-modulo">
        <div>
          <h1>Vehículos</h1>
          <p>Alta, consulta y modificación de vehículos asociados a clientes.</p>
        </div>

        <Link className="boton-enlace" to="/vehiculos/nuevo">
          + Nuevo vehículo
        </Link>
      </div>

      <form className="filtros-vehiculos" onSubmit={manejarBusqueda}>
        <label>
          Buscar
          <input
            type="search"
            value={buscar}
            onChange={(event) => setBuscar(event.target.value)}
            placeholder="Patente, marca o modelo"
          />
        </label>

        <label>
          Cliente
          <select
            value={idCliente}
            onChange={(event) => setIdCliente(event.target.value)}
          >
            <option value="">Todos</option>
            {clientes.map((cliente) => (
              <option key={cliente.idCliente} value={cliente.idCliente}>
                {cliente.nombre} {cliente.dniCuit ? `(${cliente.dniCuit})` : ''}
              </option>
            ))}
          </select>
        </label>

        <div className="acciones-filtros">
          <button type="submit">Buscar</button>
          <button type="button" onClick={limpiarFiltros}>Limpiar</button>
        </div>
      </form>

      {error && <p className="mensaje-error">{error}</p>}

      {cargando ? (
        <p>Cargando vehículos...</p>
      ) : vehiculos.length === 0 ? (
        <p>No se encontraron vehículos.</p>
      ) : (
        <div className="tabla-responsive">
          <table className="tabla-vehiculos">
            <thead>
              <tr>
                <th>ID</th>
                <th>Patente</th>
                <th>Marca</th>
                <th>Modelo</th>
                <th>Año</th>
                <th>Cliente</th>
                <th>DNI/CUIT</th>
                <th></th>
              </tr>
            </thead>

            <tbody>
              {vehiculos.map((vehiculo) => (
                <tr key={vehiculo.idVehiculo}>
                  <td>{vehiculo.idVehiculo}</td>
                  <td>{vehiculo.patente}</td>
                  <td>{vehiculo.marca}</td>
                  <td>{vehiculo.modelo}</td>
                  <td>{vehiculo.anio ?? '—'}</td>
                  <td>{vehiculo.nombreCliente}</td>
                  <td>{vehiculo.dniCuitCliente || '—'}</td>
                  <td>
                    <Link to={`/vehiculos/${vehiculo.idVehiculo}/editar`}>
                      Editar
                    </Link>
                    {' | '}
                    <Link to={`/vehiculos/${vehiculo.idVehiculo}/historial`}>
                      Historial
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
