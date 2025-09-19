import { useEffect, useId, useState } from 'react'
import Editor, { type OnMount } from '@monaco-editor/react'
import type { XsdFile } from '../types'

export default function XsdList({ xsds, setXsds }: { xsds: XsdFile[]; setXsds: (v: XsdFile[]) => void }) {
    const uid = useId()
    const [activeId, setActiveId] = useState<string>(xsds[0]?.id || '')
    const active = xsds.find((x) => x.id === activeId)

    useEffect(() => {
        if (!active && xsds[0]) setActiveId(xsds[0].id)
    }, [xsds])

    function addEmpty() {
        const n = xsds.length + 1
        const id = `${uid}-${n}`
        const item = { id, name: `schema${n}.xsd`, content: '<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"></xs:schema>' }
        setXsds([...xsds, item])
        setActiveId(id)
    }
    function removeActive() {
        if (!active) return
        const next = xsds.filter((x) => x.id !== activeId)
        setXsds(next)
        if (next[0]) setActiveId(next[0].id)
    }
    function renameActive(name: string) {
        if (!active) return
        setXsds(xsds.map((x) => (x.id === activeId ? { ...x, name } : x)))
    }
    function editActive(content: string) {
        if (!active) return
        setXsds(xsds.map((x) => (x.id === activeId ? { ...x, content } : x)))
    }

    const onMount: OnMount = (editor) => {
        // Ensure Monaco relayouts if container was 0px tall at mount time
        setTimeout(() => editor.layout(), 0)
    }

    return (
        <div className="xsd-panel">
            <div className="row header">
                <div className="xsd-tabs">
                    {xsds.map((x) => (
                        <button key={x.id} className={`xsd-tab ${x.id === activeId ? 'active' : ''}`} onClick={() => setActiveId(x.id)}>
                            {x.name}
                        </button>
                    ))}
                </div>
                <div className="actions">
                    <button className="btn" onClick={addEmpty}>Add</button>
                    <button className="btn btn--danger" onClick={removeActive} disabled={!active}>Remove</button>
                </div>
            </div>

            {active ? (
                <>
                    <input className="name" value={active.name} onChange={(e) => renameActive(e.target.value)} />
                    <div className="xsd-editor-wrap">
                        <Editor
                            key={activeId}
                            height="100%"
                            defaultLanguage="xml"
                            value={active.content}
                            onChange={(v) => editActive(v || '')}
                            onMount={onMount}
                            options={{ minimap: { enabled: false }, wordWrap: 'on', automaticLayout: true, scrollBeyondLastLine: false}}
                        />
                    </div>
                </>
            ) : (
                <div className="empty">No XSDs yet. Add one or drop a .xsd file below.</div>
            )}
        </div>
    )
}
