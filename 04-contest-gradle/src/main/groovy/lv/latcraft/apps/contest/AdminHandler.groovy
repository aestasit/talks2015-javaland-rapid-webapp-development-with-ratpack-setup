package lv.latcraft.apps.contest

import groovy.sql.Sql
import org.slf4j.Logger
import ratpack.form.Form
import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler

import static ratpack.groovy.Groovy.groovyTemplate

class AdminHandler extends GroovyHandler {

  @Override
  void handle(GroovyContext context) throws Exception {
    context.byMethod {
      get {
        render groovyTemplate('admin.html', result: "")
      }
      post { Logger logger, Sql sql ->
        def result
        Form form = parse(Form)
        String query = form?.query?.toString()
        logger.info("Executing '${query}'")
        try {
          if (query.toLowerCase().contains('select')) {
            result = sql.rows(query)
          } else {
            result = sql.execute(query)
          }
          sql.commit()
        } catch (Throwable t) {
          result = t
        }
        render groovyTemplate('admin.html', result: result)
      }
    }
  }

}
