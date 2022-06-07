namespace acme.api

use smithy4s.api#simpleRestJson

@simpleRestJson
service ReviewsService {
  version: "1.0.0",
  operations: [BestRated]
}

@http(method: "POST", uri: "/best-rated", code: 200)
operation BestRated {
  input: BestRatedInput,
  output: BestRatedOutput,
  errors: [BadInput]
}

structure BestRatedInput {
  @required
  start: Date,
  @required
  end: Date,
  @required
  limit: Integer,
  @required
  min_number_reviews: Integer
}

structure BestRatedOutput {
  @httpPayload
  @required
  results: Results
}

string Date

list Results {
  member: Result
}

structure Result {
  @required
  asin: Asin,
  @required
  average_rating: Double
}

string Asin

structure Review {
    @required
    reviewerID: String,
    @required
    asin: String,
    reviewerName: String,
    @required
    helpful: Helpfulness,
    @required
    reviewText: String,
    @required
    overall: Rating,
    @required
    summary: String,
    unixReviewTime: Long
}

@range(min:1, max: 5)
double Rating

@length(min:2, max:2)
list Helpfulness {
    member: Integer
}

@error("client")
structure BadInput {
  @required
  message: String
}
