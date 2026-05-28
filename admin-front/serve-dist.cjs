const http = require('http')
const fs = require('fs')
const path = require('path')

const root = path.join(__dirname, 'dist')
const port = Number(process.env.PORT || 4173)

const types = {
  '.html': 'text/html; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon'
}

http.createServer((request, response) => {
  const cleanUrl = decodeURIComponent(request.url.split('?')[0])
  const requested = cleanUrl === '/' ? '/index.html' : cleanUrl
  const filePath = path.join(root, requested)
  const safePath = filePath.startsWith(root) ? filePath : path.join(root, 'index.html')
  const finalPath = fs.existsSync(safePath) && fs.statSync(safePath).isFile()
    ? safePath
    : path.join(root, 'index.html')

  response.writeHead(200, {
    'content-type': types[path.extname(finalPath)] || 'application/octet-stream'
  })
  fs.createReadStream(finalPath).pipe(response)
}).listen(port, '127.0.0.1', () => {
  console.log(`admin-front preview: http://127.0.0.1:${port}`)
})
