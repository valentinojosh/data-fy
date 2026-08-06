export function containsNullOrZero(data) {
    for (const key in data) {
        const value = data[key];

        if (value === null || value === 0) {
            return true;
        }

        if (Array.isArray(value) && (value.length === 0 || value.includes(null))) {
            return true;
        }

        if (typeof value === 'object' && Object.keys(value).length === 0) {
            return true;
        }
    }
    return false;
}
