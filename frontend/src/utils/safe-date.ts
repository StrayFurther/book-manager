export function safeFormatDate(d: any) {
    try {
        const date = d ? new Date(d) : null;
        return date && !isNaN(date.getTime()) ? date.toLocaleString() : "";
    } catch {
        return "";
    }
}