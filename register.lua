-- KEYS[1] = seatKey
-- KEYS[2] = enrollKey
-- KEYS[3] = lectureKey
-- ARGV[1] = studentId
-- ARGV[2] = current timestamp

if (redis.call('EXISTS', KEYS[1]) == 0) then
    return {err = "NO_SEAT_KEY"}
end

local remaining = redis.call('DECR', KEYS[1])

if (remaining < 0) then
    redis.call('INCR', KEYS[1])
    return {err = "SOLD_OUT"}
end

if (redis.call('ZSCORE', KEYS[2], ARGV[1])) then
    redis.call('INCR', KEYS[1])
    return {err = "ALREADY_REGISTERED"}
end

redis.call('ZADD', KEYS[2], ARGV[2], ARGV[1])
redis.call('HINCRBY', KEYS[3], 'remainingSeats', -1)

return {ok = "SUCCESS"}
