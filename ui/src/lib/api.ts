import type { ValidateResponse, XsdFile } from '../types'


const API_BASE = import.meta.env.VITE_API_BASE || '' // '' -> same origin (proxied in dev)


export async function validateXml(xml: string, xsds: XsdFile[]): Promise<ValidateResponse> {
    const csrfTokenResponse = await fetch(`${API_BASE}/api/csrfToken`)
    const csrfTokenText = await csrfTokenResponse.text()
    const csrfToken = csrfTokenText.split('=')[1]
    const res = await fetch(`${API_BASE}/api/validate`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'CSRF-Token': csrfToken,
        },
        body: JSON.stringify({
            xml,
            xsds: xsds.map(({ name, content }) => ({ name, content })),
            options: { maxErrors: 100, namespaceAware: true }
        })
    })
    if (!res.ok) {
        const text = await res.text().catch(() => '')
        throw new Error(`Validate failed: ${res.status} ${res.statusText}\n${text}`)
    }
    return (await res.json()) as ValidateResponse
}