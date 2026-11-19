import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import api from '../../api/axios';
import './vehiculos.css';

const formularioVacio = {
  patente: '',
  marca: '',
  modelo: '',
  anio: '',
  idCliente: '',
};

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

export default function PaginaFormularioVehiculo() {
  const { id } = useParams();
  const navigate = useNavigate();
  const esEdicion = Boolean(id);

  const [formulario, setFormulario] = useState(formularioVacio);
  const [clientes, setClientes] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [guardando, setGuardando] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    let activo = true;

    async function cargarDatos() {
      setCargando(true);
      setError('');

      try {
        const promesas = [api.get('/clientes')];

        if (esEdicion) {
          promesas.push(api.get(`/vehiculos/${id}`));
        }

        const resultados = await Promise.all(promesas);

        if (!activo) {
          return;
        }

        setClientes(resultados[0].data);

        if (esEdicion) {
          const vehiculo = resultados[1].data;

          setFormulario({
            patente: vehiculo.patente ?? '',
            marca: vehiculo.marca ?? '',
            modelo: vehiculo.modelo ?? '',
            anio: vehiculo.anio ?? '',
            idCliente: vehiculo.idCliente ?? '',
          });
        }
      } catch (err) {
        if (activo) {
          setError(obtenerMensajeError(err));
        }
      } finally {
        if (activo) {
          setCargando(false);
        }
      }
    }

    cargarDatos();

    return () => {
      activo = false;
    };
  }, [esEdicion, id]);

  function actualizarCampo(event) {
    const { name, value } = event.target;

    setFormulario((actual) => ({
      ...actual,
      [name]: value,
    }));
  }

  async function guardar(event) {
    event.preventDefault();

    if (!formulario.patente.trim()
        || !formulario.marca.trim()
        || !formulario.modelo.trim()) {
      setError('Patente, marca y modelo son obligatorios.');
      return;
    }

    if (!formulario.idCliente) {
      setError('Debe seleccionar un cliente.');
      return;
    }

    const anio = formulario.anio === '' ? null : Number(formulario.anio);

    if (anio !== null && (!Number.isInteger(anio) || anio < 1886 || anio > 2100)) {
      setError('El año debe estar entre 1886 y 2100.');
      return;
    }

    setGuardando(true);
    setError('');

    const payload = {
      patente: formulario.patente.trim(),
      marca: formulario.marca.trim(),
      modelo: formulario.modelo.trim(),
      anio,
      idCliente: Number(formulario.idCliente),
    };

    try {
      if (esEdicion) {
        await api.put(`/vehiculos/${id}`, payload);
      } else {
        await api.post('/vehiculos', payload);
      }

      navigate('/vehiculos');
    } catch (err) {
      setError(obtenerMensajeError(err));
    } finally {
      setGuardando(false);
    }
  }

  if (cargando) {
    return (
      <main className="pagina-vehiculos">
        <p>Cargando datos...</p>
      </main>
    );
  }

  return (
    <main className="pagina-vehiculos pagina-formulario-vehiculo">
      <Link to="/vehiculos">← Volver a vehículos</Link>

      <h1>{esEdicion ? 'Editar vehículo' : 'Nuevo vehículo'}</h1>

      {error && <p className="mensaje-error">{error}</p>}

      <form className="formulario-vehiculo" onSubmit={guardar}>
        <label>
          Patente *
          <input
            name="patente"
            value={formulario.patente}
            onChange={actualizarCampo}
            maxLength={15}
            required
          />
        </label>

        <label>
          Marca *
          <input
            name="marca"
            value={formulario.marca}
            onChange={actualizarCampo}
            maxLength={60}
            required
          />
        </label>

        <label>
          Modelo *
          <input
            name="modelo"
            value={formulario.modelo}
            onChange={actualizarCampo}
            maxLength={60}
            required
          />
        </label>

        <label>
          Año
          <input
            name="anio"
            type="number"
            min="1886"
            max="2100"
            value={formulario.anio}
            onChange={actualizarCampo}
          />
        </label>

        <label>
          Cliente *
          <select
            name="idCliente"
            value={formulario.idCliente}
            onChange={actualizarCampo}
            required
          >
            <option value="">Seleccionar cliente</option>
            {clientes.map((cliente) => (
              <option key={cliente.idCliente} value={cliente.idCliente}>
                {cliente.nombre} {cliente.dniCuit ? `(${cliente.dniCuit})` : ''}
              </option>
            ))}
          </select>
        </label>

        <div className="acciones-formulario">
          <button type="submit" disabled={guardando}>
            {guardando
              ? 'Guardando...'
              : esEdicion
                ? 'Guardar cambios'
                : 'Crear vehículo'}
          </button>

          <Link to="/vehiculos">Cancelar</Link>
        </div>
      </form>
    </main>
  );
}
