import type { ValidationIssue } from '../types'

export default function IssueTable({ issues, onJump }: { issues: ValidationIssue[]; onJump: (l?: number, c?: number) => void }) {
    if (!issues.length) return <div className="issues empty">No issues 🎉</div>

    return (
        <div className="issues">
            <table>
                <thead>
                <tr>
                    <th>Severity</th>
                    <th>Message</th>
                    <th>Line</th>
                    <th>Column</th>
                    <th>File</th>
                </tr>
                </thead>
                <tbody>
                {issues.map((i, idx) => (
                    <tr key={idx} className={i.severity.toLowerCase().includes('warn') ? 'warn' : 'err'} onClick={() => onJump(i.line, i.column)}>
                        <td>{i.severity}</td>
                        <td className="msg" title={i.message}>{i.message}</td>
                        <td>{i.line ?? ''}</td>
                        <td>{i.column ?? ''}</td>
                        <td>{i.systemId ?? ''}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    )
}
