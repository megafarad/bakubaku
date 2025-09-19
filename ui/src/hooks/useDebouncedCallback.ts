import { useRef, useMemo } from 'react'

export function useDebouncedCallback<F extends (...args: any[]) => void>(fn: F, delay = 250) {
    const t = useRef<number | undefined>(undefined)
    return useMemo(
        () => ((...args: Parameters<F>) => {
            if (t.current) window.clearTimeout(t.current)
            t.current = window.setTimeout(() => fn(...args), delay)
        }),
        [fn, delay]
    )
}
