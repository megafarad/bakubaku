import { useMemo, useState } from 'react'
import { validateXml } from './lib/api'
import { useLocalStorage } from './hooks/useLocalStorage'
import { useDebouncedCallback } from './hooks/useDebouncedCallback'
import XmlEditor from './components/XmlEditor'
import XsdList from './components/XsdList'
import IssueTable from './components/IssueTable'
import DropZone from './components/DropZone'
import type { ValidateResponse, XsdFile } from './types'

const SAMPLE_XML = `<?xml version="1.0"?>
<note>
  <to>Ada</to>
  <from>Bob</from>
</note>`
const SAMPLE_XSD = `<?xml version="1.0"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xs:element name=\"note\">
    <xs:complexType>
      <xs:sequence>
        <xs:element name=\"to\" type=\"xs:string\"/>
        <xs:element name=\"from\" type=\"xs:string\"/>
      </xs:sequence>
    </xs:complexType>
  </xs:element>
</xs:schema>`

export default function App() {
    const [xml, setXml] = useLocalStorage('xml', SAMPLE_XML)
    const [xsds, setXsds] = useLocalStorage<XsdFile[]>('xsds', [
        { id: '1', name: 'simple.xsd', content: SAMPLE_XSD }
    ])
    const [resp, setResp] = useState<ValidateResponse | null>(null)
    const [busy, setBusy] = useState(false)
    const [err, setErr] = useState<string | null>(null)

    const validate = useDebouncedCallback(async () => {
        setBusy(true); setErr(null)
        try {
            const r = await validateXml(xml, xsds)
            setResp(r)
        } catch (e: any) {
            setErr(e.message || String(e))
            setResp(null)
        } finally {
            setBusy(false)
        }
    }, 50)

    const issues = useMemo(() => {
        if (!resp) return []
        return [...resp.issues]
    }, [resp])

    const reportableIssues = issues.filter(issue => ['warning', 'error', 'fatal'].includes(issue.severity));

    function onJump(line?: number, column?: number) {
        ;(window as any).xmlEditor?.reveal(line, column)
    }

    async function handleValidateClick() {
        validate() }

    function onDropXml(next: string) { setXml(next) }
    function onDropXsds(newOnes: XsdFile[]) { setXsds([...xsds, ...newOnes]) }

    return (
        <div className="app">
            <header className="topbar">
                <h1>BakuBaku - The XSD Validator</h1>
                <div className="spacer" />
                <button className="btn" onClick={() => { setXml(SAMPLE_XML); setXsds([{ id: '1', name: 'simple.xsd', content: SAMPLE_XSD }]); setResp(null) }}>Load Sample</button>
                <button className="btn primary" onClick={handleValidateClick} title="Ctrl/Cmd+Enter">{busy ? 'Validating…' : 'Validate'}</button>
            </header>

            <main className="layout">
                {/* Top row: two equal editor panels */}
                <div className="two-col">
                    <section className="panel editor-panel">
                        <div className="row header"><h3>XML</h3></div>
                        <XmlEditor value={xml} onChange={setXml} issues={issues} />
                    </section>

                    <section className="panel editor-panel">
                        <div className="row header"><h3>XSD</h3></div>
                        <XsdList xsds={xsds} setXsds={setXsds} />
                    </section>
                </div>

                {/* Middle: drag & drop (full width, below both editors) */}
                <div className="panel">
                    <DropZone onXml={onDropXml} onXsd={onDropXsds} />
                </div>

                {/* Bottom: results (full width, below dropzone) */}
                <div className="panel">
                    <div className="row header">
                        <h3>Results</h3>
                        {resp && (
                            <div className={`pill ${resp.valid ? 'ok' : 'bad'}`}>
                                {resp.valid ? '✅ Valid' : `❌ ${reportableIssues.length} issue(s)`}
                            </div>
                        )}
                    </div>
                    {err && <div className="error">{err}</div>}
                    {resp && (
                        <>
                            <IssueTable issues={issues} onJump={onJump} />
                            <div className="stats">
                                {Object.entries(resp.stats).map(([k, v]) => (
                                    <span key={k}><b>{k}</b>: {v}</span>
                                ))}
                            </div>
                        </>
                    )}
                    {!resp && !err && <div className="empty">Click <b>Validate</b> to see results.</div>}
                </div>
            </main>

            <footer className="foot">
                <small>v{(globalThis as any).__APP_VERSION__} · Ctrl/Cmd+Enter to validate · Never uploads files automatically</small>
            </footer>
        </div>
    )
}
