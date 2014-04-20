package controllers

import controllers.Application
import play.api.http.HeaderNames
import play.api.mvc.{Request, AnyContent}
import play.api.test.{PlaySpecification, FakeApplication, FakeRequest}
import securesocial.testkit.WithLoggedUser
import org.junit.runner._
import org.specs2.runner._
import org.specs2.mutable.Specification
import org.specs2.matcher.ShouldMatchers
import play.api.test.WithApplication
import securesocial.core._
import securesocial.testkit.SocialUserGenerator

@RunWith(classOf[JUnitRunner])
class ApplicationControllerSpec extends PlaySpecification with ShouldMatchers {
  import WithLoggedUser._
  
  def minimalApp = FakeApplication(
      withoutPlugins=excludedPlugins,
      additionalPlugins =  includedPlugins++List("securesocial.core.DefaultIdGenerator")
  )
  
  "Access secured index " in new WithLoggedUser(minimalApp) {
    val req: Request[AnyContent] = FakeRequest().
      withHeaders((HeaderNames.CONTENT_TYPE, "application/x-www-form-urlencoded")).
      withCookies(cookie) // Fake cookie from the WithloggedUser trait

    val result = Application.index.apply(req)
    val actual: Int= status(result)
    actual must be equalTo OK
  }
  
  def directApp = FakeApplication(
      withoutPlugins = excludedPlugins,
      additionalPlugins =  List(
    	  "securesocial.testkit.AlwaysValidIdentityProvider",
          "securesocial.core.DefaultIdGenerator"
      )
  )
  
  "A logged in user can view the index" in new WithApplication(directApp) {
    val creds1 = cookies(route(FakeRequest(POST, "/authenticate/naive").withTextBody("user")).get)
    val Some(response)=route(FakeRequest(GET, "/").withCookies(creds1.get("id").get))
    status(response) must equalTo(OK)
  }
  
  
}