import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../../api/axios';

// TODO: reemplazar por el usuario del AuthContext cuando esté el login
const ID_USUARIO_RESPONSABLE = 1;

export default function PaginaCrearOrden() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    idVehiculo: '',
    idMecanico: '',
    kilometrajeIngreso: '',
    motivoIngreso: '',
    observaciones: '',
  });
  const [enviando, setEnviando] = useState(false);
  const [error, setError] = useState(null);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setEnviando(true);
    try {
      const body = {
        idVehiculo: Number(form.idVehiculo),
        idMecanico: form.idMecanico ? Number(form.idMecanico) : null,
        kilometrajeIngreso: Number(form.kilometrajeIngreso),
        motivoIngreso: form.motivoIngreso.trim(),
        observaciones: form.observaciones.trim() || null,
        idUsuarioResponsable: ID_USUARIO_RESPONSABLE,
      };
      const { data } = await api.post('/ordenes', body);
      navigate(`/ordenes/${data.idOrden}`);
    } catch (e) {
      const detalle = e.response?.data?.detalle
        || Object.values(e.response?.data || {}).join(' · ')
        || e.message;
      setError(detalle);
    } finally {
      setEnviando(false);
    }
  };

  return (
    <div style={{ padding: 20, maxWidth: 600, margin: '0 auto' }}>
      <Link to="/ordenes">← Volver a la lista</Link>
      <h1>Nueva orden de trabajo</h1>

      {error && <p style={{ color: 'red' }}>{error}</p>}

      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: 12 }}>
          <label>ID Vehículo *</label><br />
          <input
            type="number"
            name="idVehiculo"
            value={form.idVehiculo}
            onChange={handleChange}
            required
            min="1"
            style={{ width: '100%', padding: 6 }}
          />
        </div>

        <div style={{ marginBottom: 12 }}>
          <label>ID Mecánico (opcional)</label><br />
          <input
            type="number"
            name="idMecanico"
            value={form.idMecanico}
            onChange={handleChange}
            min="1"
            style={{ width: '100%', padding: 6 }}
          />
        </div>

        <div style={{ marginBottom: 12 }}>
          <label>Kilometraje de ingreso *</label><br />
          <input
            type="number"
            name="kilometrajeIngreso"
            value={form.kilometrajeIngreso}
            onChange={handleChange}
            required
            min="0"
            style={{ width: '100%', padding: 6 }}
          />
        </div>

        <div style={{ marginBottom: 12 }}>
          <label>Motivo de ingreso * (máx. 1000)</label><br />
          <textarea
            name="motivoIngreso"
            value={form.motivoIngreso}
            onChange={handleChange}
            required
            maxLength={1000}
            rows={3}
            style={{ width: '100%', padding: 6 }}
          />
        </div>

        <div style={{ marginBottom: 12 }}>
          <label>Observaciones (máx. 2000)</label><br />
          <textarea
            name="observaciones"
            value={form.observaciones}
            onChange={handleChange}
            maxLength={2000}
            rows={3}
            style={{ width: '100%', padding: 6 }}
          />
        </div>

        <button type="submit" disabled={enviando} style={{ padding: '8px 16px' }}>
          {enviando ? 'Creando...' : 'Crear orden'}
        </button>
      </form>
    </div>
  );
}