package co.renanbezerra.netflixremake.model

data class MovieDetail(
    val movie: Movie,
    val similars: List<Movie>
)
