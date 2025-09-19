import type { XsdFile } from '../types'

export default function DropZone({ onXml, onXsd }: { onXml: (xml: string) => void; onXsd: (files: XsdFile[]) => void }) {
    function prevent(e: React.DragEvent) {
        e.preventDefault(); e.stopPropagation()
    }
    async function onDrop(e: React.DragEvent) {
        prevent(e)
        const files = Array.from(e.dataTransfer.files)
        const xmls: string[] = []
        const xsds: XsdFile[] = []

        await Promise.all(files.map(async (f, i) => {
            const text = await f.text()
            if (f.name.toLowerCase().endsWith('.xml')) xmls.push(text)
            else if (f.name.toLowerCase().endsWith('.xsd')) xsds.push({ id: `${Date.now()}-${i}`, name: f.name, content: text })
        }))

        if (xmls[0]) onXml(xmls[0])
        if (xsds.length) onXsd(xsds)
    }

    return (
        <div className="dropzone" onDragEnter={prevent} onDragOver={prevent} onDrop={onDrop}>
            Drag & drop .xml and .xsd files here
        </div>
    )
}
