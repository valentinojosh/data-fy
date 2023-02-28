//
// const app = express()
// app.use(cors())
// app.use(bodyParser.json())
// app.use(bodyParser.urlencoded({ extended: true }))
//
// app.post("/login", (req, res) => {
//     const code = req.body.code
//     const spotifyApi = new SpotifyWebApi({
//         redirectUri: process.env.REDIRECT_URI,
//         clientId: process.env.CLIENT_ID,
//         clientSecret: process.env.CLIENT_SECRET,
//     })
//
//     spotifyApi
//         .authorizationCodeGrant(code)
//         .then(data => {
//             res.json({
//                 accessToken: data.body.access_token,
//                 refreshToken: data.body.refresh_token,
//                 expiresIn: data.body.expires_in,
//             })
//         })
//         .catch(err => {
//             res.sendStatus(400)
//         })
// })
//
// app.listen(3001)


//reference codes: post from react to backend point
// useEffect(() => {
//     axios
//         .post("http://localhost:8080/api/login", {
//             code,
//         })
//         .then(res => {
//             //what do we get back? res.data.dataName
//             console.log(res.data.msg)
//             window.history.pushState({}, null, "/")
//         })
//         .catch(() => {
//             window.location = "/"
//         })
// }, [code])