import { useRef, useEffect } from 'react'
import Editor, {type OnMount } from '@monaco-editor/react'
import type * as monaco from 'monaco-editor'
import type { ValidationIssue } from '../types'

export type XmlEditorHandle = {
    reveal: (line?: number, column?: number) => void
}

export default function XmlEditor({
                                      value,
                                      onChange,
                                      issues
                                  }: {
    value: string
    onChange: (next: string) => void
    issues: ValidationIssue[]
}) {
    const editorRef = useRef<monaco.editor.IStandaloneCodeEditor | null>(null)
    const decorationsRef = useRef<string[]>([])

    const onMount: OnMount = (editor) => {
        editorRef.current = editor
        editor.updateOptions({
            wordWrap: 'on',
            minimap: { enabled: false },
            automaticLayout: true,
            scrollBeyondLastLine: false
        })
    }

    // Decorate lines with issues
    useEffect(() => {
        const ed = editorRef.current
        if (!ed) return

        const newDecos = issues
            .filter((i) => i.line && i.column)
            .map((i) => ({
                range: new (window as any).monaco.Range(i.line!, 1, i.line!, 1),
                options: {
                    isWholeLine: true,
                    linesDecorationsClassName: i.severity === 'warning' ? 'gutter-warn' : 'gutter-error',
                    className: i.severity === 'warning' ? 'line-warn' : 'line-error',
                    hoverMessage: { value: `**${i.severity.toUpperCase()}**: ${i.message}` }
                }
            }))

        decorationsRef.current = ed.deltaDecorations(decorationsRef.current, newDecos)
    }, [issues])

    // Expose a helper on window for quick debugging
    useEffect(() => {
        ;(window as any).xmlEditor = {
            reveal(line?: number, column?: number) {
                const ed = editorRef.current
                if (!ed || !line) return
                ed.revealLineInCenter(line)
                ed.setPosition({ lineNumber: line, column: column || 1 })
                ed.focus()
            }
        } as XmlEditorHandle
    }, [])

    return (
        <div className="editor-wrap">
            <Editor
                height="100%"
                defaultLanguage="xml"
                value={value}
                onChange={(v) => onChange(v || '')}
                onMount={onMount}
                options={{ tabSize: 2 }}
            />
        </div>
    )
}
