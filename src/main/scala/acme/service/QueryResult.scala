package acme
package service

import api.Asin
import api.Rating
import acme.api.Review

final case class QueryResult(asin: String, averageRating: Double)
