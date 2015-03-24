import com.fasterxml.jackson.databind.JsonNode
import groovy.sql.Sql
import lv.latcraft.apps.contest.AdminHandler
import lv.latcraft.apps.contest.TaskRequest
import lv.latcraft.apps.contest.TaskRequestQueue
import lv.latcraft.apps.contest.ServiceModule
import lv.latcraft.apps.contest.TaskResultDAO
import org.slf4j.Logger
import ratpack.form.Form
import ratpack.groovy.templating.TemplatingModule
import ratpack.jackson.JacksonModule

import static ratpack.groovy.Groovy.groovyTemplate
import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.json
import static ratpack.jackson.Jackson.jsonNode
import static ratpack.registry.Registries.just
import static lv.latcraft.apps.contest.TaskRequestProcessor.*

ratpack {

  bindings {
    add new JacksonModule()
    add new ServiceModule()
    add(TemplatingModule) { TemplatingModule.Config config -> config.staticallyCompile = true }
  }

  handlers { Sql sql, TaskRequestQueue queue, TaskResultDAO results, Logger logger ->

    assets "public"

    get {
      render file("public/index.html")
    }

    get('search') {
      render json(VALID_RESPONSE_JSON)
    }

    prefix('api/v1') {
      handler {
        logger.info "${request.method} ${request.path}"
        next()
      }
      get('queue/all') {
        render json(requests: queue.all, response: 'OK')
      }
      get('result/top') {
        render json(results: results.top, response: 'OK')
      }
      get('result/last') {
        render json(results: results.last, response: 'OK')
      }
      handler {
        next(just(JsonNode, parse(jsonNode())))
      }
      post('queue/submit') { JsonNode jsonRequest ->
        queue.push(new TaskRequest(
          userName: jsonRequest.get('userName')?.asText(),
          processing: true,
          solutionHostName: jsonRequest.get('solutionHostName')?.asText()
        ))
        render json(response: 'OK')
      }
    }

    handler("admin", AdminHandler)

  }

}
