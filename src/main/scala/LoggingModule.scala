package io.buildo.base

import scala.language.experimental.macros
import io.buildo.ingredients.logging._

trait LoggingModule {
  def logger(name: String): Logger
  def nameOf[T]: String = macro Logger.nameOf[T]
}

class Slf4jLogger(logger: Logger) extends org.slf4j.helpers.MarkerIgnoringBase
    with org.slf4j.Logger {

  import org.slf4j.helpers.MessageFormatter

  def debug(msg: String, cause: Throwable): Unit =
    logger.debug(msg, cause)
  def debug(msg: String, params: Object*): Unit =
    debug(MessageFormatter.arrayFormat(msg, params.toArray).getMessage())
  def debug(msg: String, param1: Object, param2: Object): Unit =
    debug(MessageFormatter.format(msg, param1, param2).getMessage())
  def debug(msg: String, param: Object): Unit =
    debug(MessageFormatter.format(msg, param).getMessage())
  def debug(msg: String): Unit =
    logger.debug(msg)
  def error(msg: String, cause: Throwable): Unit =
    logger.error(msg, cause)
  def error(msg: String, params: Object*): Unit =
    error(MessageFormatter.arrayFormat(msg, params.toArray).getMessage())
  def error(msg: String, param1: Object, param2: Object): Unit =
    error(MessageFormatter.format(msg, param1, param2).getMessage())
  def error(msg: String, param: Object): Unit =
    error(MessageFormatter.format(msg, param).getMessage())
  def error(msg: String): Unit =
    logger.error(msg)
  def info(msg: String, cause: Throwable): Unit =
    logger.info(msg, cause)
  def info(msg: String, params: Object*): Unit =
    info(MessageFormatter.arrayFormat(msg, params.toArray).getMessage())
  def info(msg: String, param1: Object, param2: Object): Unit =
    info(MessageFormatter.format(msg, param1, param2).getMessage())
  def info(msg: String, param: Object): Unit =
    info(MessageFormatter.format(msg, param).getMessage())
  def info(msg: String): Unit =
    logger.info(msg)
  def warn(msg: String, cause: Throwable): Unit =
    logger.warn(msg, cause)
  def warn(msg: String, params: Object*): Unit =
    warn(MessageFormatter.arrayFormat(msg, params.toArray).getMessage())
  def warn(msg: String, param1: Object, param2: Object): Unit =
    warn(MessageFormatter.format(msg, param1, param2).getMessage())
  def warn(msg: String, param: Object): Unit =
    warn(MessageFormatter.format(msg, param).getMessage())
  def warn(msg: String): Unit =
    logger.warn(msg)
  def trace(msg: String, cause: Throwable): Unit =
    logger.debug(msg, cause)
  def trace(msg: String, params: Object*): Unit =
    trace(MessageFormatter.arrayFormat(msg, params.toArray).getMessage())
  def trace(msg: String, param1: Object, param2: Object): Unit =
    trace(MessageFormatter.format(msg, param1, param2).getMessage())
  def trace(msg: String, param: Object): Unit =
    trace(MessageFormatter.format(msg, param).getMessage())
  def trace(msg: String): Unit =
    logger.debug(msg)
  def isDebugEnabled(): Boolean = logger.underlying.isEnabled(Level.Debug)
  def isErrorEnabled(): Boolean = logger.underlying.isEnabled(Level.Error)
  def isWarnEnabled(): Boolean = logger.underlying.isEnabled(Level.Warn)
  def isInfoEnabled(): Boolean = logger.underlying.isEnabled(Level.Info)
  def isTraceEnabled(): Boolean = logger.underlying.isEnabled(Level.Debug)
}

trait IngLoggingModule extends LoggingModule
  with ConfigModule {

  private case class LoggingConfig(
    debugEnabled: Boolean)

  private val loggingConfig = config.get { conf =>
    LoggingConfig(
      debugEnabled = (conf.hasPath(s"$projectName.logging.debug") &&
        conf.getBoolean(s"$projectName.logging.debug"))
    )
  }

  private val transports = Seq(new transport.Console())

  override def logger(name: String): Logger = Logger(name, transports, {
    case Level.Debug => loggingConfig.debugEnabled
    case _ => true
  })

  org.slf4j.impl.SimpleLoggerFactory.setLoggerFactoryInterface(
    new org.slf4j.impl.LoggerFactoryInterface {
      override def getNewLogger(name: String) = new Slf4jLogger(
        logger(name))
    })
}

