import { useState } from 'react';

export default function FormTrabajo({ onAgregar, deshabilitado }) {
  const [descripcion, setDescripcion] = useState('');
  const [enviando, setEnviando] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!descripcion.trim()) return;
    setEnviando(true);
    try {
      await onAgregar(descripcion.trim());
      setDescripcion('');
    } finally {
      setEnviando(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ marginTop: 12 }}>
      <input
        type="text"
        placeholder="Descripción del trabajo realizado"
        value={descripcion}
        onChange={(e) => setDescripcion(e.target.value)}
        disabled={deshabilitado || enviando}
        style={{ width: '70%', padding: 6, marginRight: 8 }}
      />
      <button type="submit" disabled={deshabilitado || enviando || !descripcion.trim()}>
        Agregar trabajo
      </button>
    </form>
  );
}