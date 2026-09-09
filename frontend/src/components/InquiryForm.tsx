import { useState, type FormEvent } from "react";
import { createInquiry } from "../api";

type InquiryFormProps = {
  propertyId: number;
};

type InquiryFields = {
  name: string;
  email: string;
  phone: string;
  message: string;
};

const initialFields: InquiryFields = {
  name: "",
  email: "",
  phone: "",
  message: "",
};

export function InquiryForm({ propertyId }: InquiryFormProps) {
  const [fields, setFields] = useState(initialFields);
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState("");
  const [success, setSuccess] = useState(false);

  function update(name: keyof InquiryFields, value: string) {
    setFields((current) => ({ ...current, [name]: value }));
    setFormError("");
    setSuccess(false);
  }

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (submitting) return;

    const nextError = validate(fields);
    if (nextError) {
      setFormError(nextError);
      return;
    }

    setSubmitting(true);
    setFormError("");
    setSuccess(false);

    try {
      await createInquiry(propertyId, {
        name: fields.name.trim(),
        email: fields.email.trim(),
        phone: fields.phone.trim(),
        message: fields.message.trim(),
      });
      setSuccess(true);
      setFields(initialFields);
    } catch (err) {
      setFormError(err instanceof Error ? err.message : "No pudimos enviar la consulta.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form className="contact-panel" aria-label="Consulta" onSubmit={submit}>
      <p className="eyebrow">Consulta</p>
      <h2>Coordina una visita</h2>
      <p>Deja tus datos y el publicador puede responderte por mail o telefono.</p>

      <div className="contact-fields">
        <label>
          <span>Nombre</span>
          <input
            value={fields.name}
            onChange={(event) => update("name", event.target.value)}
            maxLength={100}
            autoComplete="name"
            required
          />
        </label>
        <label>
          <span>Email</span>
          <input
            value={fields.email}
            onChange={(event) => update("email", event.target.value)}
            type="email"
            maxLength={150}
            autoComplete="email"
            required
          />
        </label>
        <label>
          <span>Telefono</span>
          <input
            value={fields.phone}
            onChange={(event) => update("phone", event.target.value)}
            maxLength={30}
            autoComplete="tel"
          />
        </label>
        <label>
          <span>Mensaje</span>
          <textarea
            value={fields.message}
            onChange={(event) => update("message", event.target.value)}
            maxLength={1000}
            rows={5}
            required
          />
        </label>
      </div>

      {formError && (
        <p className="form-message form-message-error" role="alert">
          {formError}
        </p>
      )}
      {success && (
        <p className="form-message form-message-success" role="status">
          Consulta enviada. El publicador ya tiene tus datos.
        </p>
      )}
      <button type="submit" disabled={submitting}>
        {submitting ? "Enviando..." : "Consultar propiedad"}
      </button>
    </form>
  );
}

function validate(fields: InquiryFields) {
  if (!fields.name.trim()) return "Completa tu nombre.";
  if (!fields.email.trim()) return "Completa tu email.";
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(fields.email.trim())) return "Ingresa un email valido.";
  if (!fields.message.trim()) return "Escribi un mensaje para el publicador.";
  return "";
}
