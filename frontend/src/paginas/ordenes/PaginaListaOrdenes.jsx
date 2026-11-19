import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../../api/axios';
import EtiquetaEstado from '../../componentes/comunes/EtiquetaEstado';

const ESTADOS = [
  'RECIBIDO', 'EN_DIAGNOSTICO', 'EN_REPARACION',
  'ESPERANDO_REPUESTO', 'CONTROL_FINAL', 'FINALIZADO', 'ENTREGADO',
];

export default function PaginaListaOrdenes() {
  const [ordenes, setOrdenes] = useState([]);
  const [estado, setEstado] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let activo = true;
    const url = estado ? `/ordenes?estado=${estado}` : '/ordenes';
    api.get(url)
      .then((r) => { if (activo) setOrdenes(r.data); })
      .catch((e) => { if (activo) setError(e.response?.data?.detalle || e.message); })
      .finally(() => { if (activo) setLoading(false); });
    return () => { activo = false; };
  }, [estado]);

  if (loading) return <p>Cargando...</p>;
  if (error) return <p style={{ color: 'red' }}>Error: {error}</p>;

  return (
    <div style={{ padding: 20 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1>Órdenes de trabajo</h1>
        <Link to="/ordenes/nueva">
          <button style={{ padding: '8px 16px' }}>+ Nueva orden</button>
        </Link>
      </div>
      <div style={{ marginBottom: 10 }}>
        <label>Filtrar por estado: </label>
        <select
          value={estado}
          onChange={(e) => {
            setLoading(true);
            setError(null);
            setEstado(e.target.value);
          }}
        >
          <option value="">Todos</option>
          {ESTADOS.map((s) => <option key={s} value={s}>{s.replaceAll('_', ' ')}</option>)}
        </select>
      </div>
      {ordenes.length === 0 ? (
        <p>No hay órdenes.</p>
      ) : (
        <div className="tabla-contenedor">
          <table className="tabla">
            <thead>
              <tr>
                <th>ID</th>
                <th>Patente</th>
                <th>Cliente</th>
                <th>Estado</th>
                <th>Fecha ingreso</th>
                <th style={{ textAlign: 'right' }}></th>
              </tr>
            </thead>
            <tbody>
              {ordenes.map((o) => (
                <tr key={o.idOrden}>
                  <td className="texto-mono" style={{ fontWeight: 700 }}>{o.idOrden}</td>
                  <td>{o.patenteVehiculo ?? '—'}</td>
                  <td>{o.nombreCliente ?? '—'}</td>
                  <td><EtiquetaEstado estado={o.estado} /></td>
                  <td className="texto-mono texto-muted">
                    {o.fechaIngreso?.replace('T', ' ').slice(0, 16) ?? '—'}
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    <Link to={`/ordenes/${o.idOrden}`} className="link-boton">Ver</Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}