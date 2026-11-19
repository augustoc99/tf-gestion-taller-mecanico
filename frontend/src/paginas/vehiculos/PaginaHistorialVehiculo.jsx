import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import api from '../../api/axios';
import './vehiculos.css';

function formatearFecha(fecha) {
  if (!fecha) {
    return '—';
  }

  return fecha.replace('T', ' ').slice(0, 16);
}

function formatearEstado(estado) {
  if (!estado) {
    return '—';
  }

  return estado.replaceAll('_', ' ');
}

export default function PaginaHistorialVehiculo() {
  const { id } = useParams();

  const [vehiculo, setVehiculo] = useState(null);
  const [ordenes, setOrdenes] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let activo = true;

    Promise.all([
      api.get(`/vehiculos/${id}`),
      api.get(`/ordenes/vehiculo/${id}`),
    ])
      .then(([respuestaVehiculo, respuestaOrdenes]) => {
        if (!activo) {
          return;
        }

        setVehiculo(respuestaVehiculo.data);
        setOrdenes(respuestaOrdenes.data);
      })
      .catch((err) => {
        if (!activo) {
          return;
        }

        setError(
          err.response?.data?.detalle
            || err.response?.data?.message
            || err.message
            || 'No se pudo cargar el historial del vehículo.'
        );
      })
      .finally(() => {
        if (activo) {
          setCargando(false);
        }
      });

    return () => {
      activo = false;
    };
  }, [id]);

  if (cargando) {
    return (
      <main className="pagina-vehiculos">
        <p>Cargando historial...</p>
      </main>
    );
  }

  if (error) {
    return (
      <main className="pagina-vehiculos">
        <Link to="/vehiculos">← Volver a vehículos</Link>
        <p className="mensaje-error">{error}</p>
      </main>
    );
  }

  return (
    <main className="pagina-vehiculos">
      <p>
        <Link to="/vehiculos">← Volver a vehículos</Link>
      </p>

      <div className="cabecera-modulo">
        <div>
          <h1>Historial del vehículo</h1>

          {vehiculo && (
            <p>
              <strong>{vehiculo.patente}</strong>
              {' — '}
              {vehiculo.marca} {vehiculo.modelo}
              {' — '}
              {vehiculo.nombreCliente}
            </p>
          )}
        </div>
      </div>

      {ordenes.length === 0 ? (
        <p>Este vehículo todavía no registra intervenciones.</p>
      ) : (
        <div className="tabla-responsive">
          <table className="tabla-vehiculos">
            <thead>
              <tr>
                <th>Orden</th>
                <th>Fecha ingreso</th>
                <th>Kilometraje</th>
                <th>Motivo</th>
                <th>Diagnóstico</th>
                <th>Estado</th>
                <th>Mecánico</th>
                <th>Fecha entrega</th>
                <th></th>
              </tr>
            </thead>

            <tbody>
              {ordenes.map((orden) => (
                <tr key={orden.idOrden}>
                  <td>#{orden.idOrden}</td>
                  <td>{formatearFecha(orden.fechaIngreso)}</td>
                  <td>{orden.kilometrajeIngreso ?? '—'}</td>
                  <td>{orden.motivoIngreso || '—'}</td>
                  <td>{orden.diagnostico || '—'}</td>
                  <td>{formatearEstado(orden.estado)}</td>
                  <td>{orden.nombreMecanico || 'Sin asignar'}</td>
                  <td>{formatearFecha(orden.fechaEntrega)}</td>
                  <td>
                    <Link to={`/ordenes/${orden.idOrden}`}>
                      Ver orden
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