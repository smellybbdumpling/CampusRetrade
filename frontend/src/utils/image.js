export function buildImageUrl(path, fallbackText = 'Image') {
  if (!path) {
    return `https://placehold.co/640x460?text=${encodeURIComponent(fallbackText)}`
  }
  return path.startsWith('http') ? path : path
}

export function handleImageError(event, fallbackText = 'Image') {
  event.target.src = `https://placehold.co/640x460?text=${encodeURIComponent(fallbackText)}`
}