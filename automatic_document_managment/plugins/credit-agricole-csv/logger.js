const { createLogger, format, transports } = require('winston');

var options = {
    console: {
        level: 'debug',
        handleExceptions: true,
        json: false,
        colorize: true,
    },
};

const logger = createLogger({
    format: format.combine(
        format.colorize(),
        format.timestamp({
            format: 'YYYY-MM-DD HH:mm:ss'
        }),
        format.printf(
            info => `${info.timestamp} ${info.level}: ${info.message}`
        )
    ),
    transports: [
        new transports.Console(options.console)
    ],
    exitOnError: false, // do not exit on handled exceptions
});

module.exports = {
    logger
};
