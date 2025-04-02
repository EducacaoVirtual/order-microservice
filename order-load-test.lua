-- Test Command
-- wrk -c 50 -t 2 -d 30s -s ./order-load-test.lua --latency http://localhost:8083/orders

setup = function(thread)
    math.randomseed(os.time())
end

local function generate_uuid()
    local template = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'
    return string.gsub(template, '[xy]', function(c)
        local v = (c == 'x') and math.random(0, 15) or math.random(8, 11)
        return string.format('%x', v)
    end)
end

local function table_to_json(tbl)
    local function serialize(val)
        if type(val) == "table" then
            local is_array = #val > 0  -- Check if the table is an array

            local str = is_array and "[" or "{"
            for k, v in pairs(val) do
                local key = is_array and "" or string.format("\"%s\":", k)
                str = str .. key .. serialize(v) .. ","
            end
            return str:sub(1, -2) .. (is_array and "]" or "}") -- Remove the last comma and close properly
        elseif type(val) == "string" then
            return string.format("\"%s\"", val)
        else
            return tostring(val)
        end
    end
    return serialize(tbl)
end

request = function()
    local path = "/orders"

    -- Generate a UUID for customerId
    local customerId = generate_uuid()

    -- Create a list of items correctly formatted as a JSON array
    local items = {}
    local totalAmount = 0
    for i = 1, math.random(1, 5) do  -- Generate 1 to 5 items per order
        local quantity = math.random(1, 10)  -- Quantity between 1 and 10
        local price = math.random(100, 1000) / 100  -- Price between 1.00 and 10.00
        totalAmount = totalAmount + (quantity * price)

        table.insert(items, {
            productId = generate_uuid(),
            quantity = quantity
        })
    end

    -- Create the request body with the correct `items` list
    local body = table_to_json({
        customerId = customerId,
        items = items,  -- Now correctly formatted as an array
        totalAmount = string.format("%.2f", totalAmount) -- Format as a String
    })

    -- Configure method and headers
    wrk.method = "POST"
    wrk.headers["Content-Type"] = "application/json"

    return wrk.format("POST", path, nil, body)
end

-- Print latency
done = function(summary, latency, requests)
    io.write("--------------------------------\n")
    local percentiles = {50, 90, 99, 99.999}
    for _, v in ipairs(percentiles) do
        local n = latency:percentile(v)
        io.write(string.format("%gth percentile: %d ms\n", v, n))
    end
    io.write("--------------------------------\n")
end
