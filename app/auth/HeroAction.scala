package auth

import javax.inject.Inject
import pdi.jwt._
import play.api.http.HeaderNames
import play.api._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import play.api.cache.AsyncCacheApi
import models.Hero
import services.GameService

// A custom request type to hold our JWT claims, we can pass these on to the
// handling action
case class HeroRequest[A](jwt: JwtClaim, token: String, userId: String, hero: Hero, request: Request[A]) extends WrappedRequest[A](request)

// Our custom action implementation
class HeroAction @Inject() (bodyParser: BodyParsers.Default, authService: AuthService, cache: AsyncCacheApi, gameService: GameService)(implicit ec: ExecutionContext) extends ActionBuilder[HeroRequest, AnyContent] {

  override def parser: BodyParser[AnyContent]               = bodyParser
  override protected def executionContext: ExecutionContext = ec

  // A regex for parsing the Authorization header value
  private val headerTokenRegex = """Bearer (.+?)""".r

  // Called when a request is invoked. We should validate the bearer token here
  // and allow the request to proceed if it is valid.
  override def invokeBlock[A](request: Request[A], block: HeroRequest[A] => Future[Result]): Future[Result] =
    extractBearerToken(request) map { token =>
      authService.validateJwt(token) match {
        case Success(claim) => {
          claim.subject match {
            case Some(value) => {
              cache
                .get[Int](s"${value}-active_hero")
                .map(maybeActive => {
                  maybeActive match {
                    case Some(heroId) => {
                      gameService
                        .loadHero(value, heroId)
                        .map(maybeHero => {
                          maybeHero match {
                            case Some(hero) => block(HeroRequest(claim, token, value, hero, request))
                            case None       => Future.successful(Results.Unauthorized)
                          }
                        })
                        .flatten
                    }
                    case None => Future.successful(Results.Unauthorized)
                  }
                })
                .flatten
            }
            case None => Future.successful(Results.Unauthorized("Missing id"))
          }
        } // token was valid - proceed!
        case Failure(t) => Future.successful(Results.Unauthorized(t.getMessage)) // token was invalid - return 401
      }
    } getOrElse Future.successful(Results.Unauthorized) // no token was sent - return 401

  // Helper for extracting the token value
  private def extractBearerToken[A](request: Request[A]): Option[String] =
    request.headers.get(HeaderNames.AUTHORIZATION) collect { case headerTokenRegex(token) =>
      token
    }
}
