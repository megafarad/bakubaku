export type ValidationIssue = {
    severity: 'warning' | 'error' | 'fatal' | string
    message: string
    line?: number
    column?: number
    systemId?: string
}


export type ValidateResponse = {
    valid: boolean
    issues: ValidationIssue[]
    stats: Record<string, number>
}


export type XsdFile = { id: string; name: string; content: string }