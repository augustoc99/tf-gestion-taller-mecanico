import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../../api/axios';

const ESTADOS = [
  'RECIBIDO', 'EN_DIAGNOSTICO', 'EN_REPARACION',
  'ESPERANDO_REPUESTO', 'CONTROL_FINAL', 'FINALIZADO', 'ENTREGADO',
];

export default function OrdenesListPage() {
  const [ordenes, setOrdenes] = useState([]);
  const [estado, setEstado] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let activo = true;
    const url = estado ? `/ordenes?estado=${estado}` : '/ordenes';
    api.get(url)
      .then((r) => { if (activo) setOrdenes(r.data); })
      .catch((e) => { if (activo) setError(e.message); })
      .finally(() => { if (activo) setLoading(false); });
    return () => { activo = false; };
  }, [estado]);

  if (loading) return <p>Cargando...</p>;
  if (error) return <p style={{ color: 'red' }}>Error: {error}</p>;

  return (
    <div style={{ padding: 20 }}>
      <h1>Órdenes de trabajo</h1>
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
          {ESTADOS.map((s) => <option key={s} value={s}>{s}</option>)}
        </select>
      </div>
      {ordenes.length === 0 ? (
        <p>No hay órdenes.</p>
      ) : (
        <table border="1" cellPadding="8" style={{ borderCollapse: 'collapse' }}>
          <thead>
            <tr>
              <th>ID</th>
              <th>Vehículo</th>
              <th>Estado</th>
              <th>Fecha ingreso</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {ordenes.map((o) => (
              <tr key={o.idOrden}>
                <td>{o.idOrden}</td>
                <td>{o.idVehiculo ?? '—'}</td>
                <td>{o.estado}</td>
                <td>{o.fechaIngreso}</td>
                <td><Link to={`/ordenes/${o.idOrden}`}>Ver</Link></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}