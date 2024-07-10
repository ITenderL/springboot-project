package com.itender.redis.util;

import com.google.common.collect.Lists;
import com.itender.redis.constant.RedisConstants;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;
import java.util.SortedMap;

/**
 * @author yuanhewei
 * @date 2024/7/9 14:35
 * @description
 */
public class DoubleCacheUtil {

    public static String parse(String elString, SortedMap<String, Object> map) {
        elString = String.format("#{%s}", elString);
        //创建表达式解析器
        ExpressionParser parser = new SpelExpressionParser();
        //通过evaluationContext.setVariable可以在上下文中设定变量。
        EvaluationContext context = new StandardEvaluationContext();
        map.forEach(context::setVariable);
        //解析表达式
        Expression expression = parser.parseExpression(elString, new TemplateParserContext());
        //使用Expression.getValue()获取表达式的值，这里传入了Evaluation上下文
        return expression.getValue(context, String.class);
    }

    public static String arrayParse(List<String> elStrings, SortedMap<String, Object> map) {
        List<String> result = Lists.newArrayList();
        elStrings.forEach(elString -> {
            elString = String.format("#{%s}", elString);
            //创建表达式解析器
            ExpressionParser parser = new SpelExpressionParser();
            //通过evaluationContext.setVariable可以在上下文中设定变量。
            EvaluationContext context = new StandardEvaluationContext();
            map.forEach(context::setVariable);
            //解析表达式
            Expression expression = parser.parseExpression(elString, new TemplateParserContext());
            //使用Expression.getValue()获取表达式的值，这里传入了Evaluation上下文
            result.add(expression.getValue(context, String.class));
        });
        return String.join(RedisConstants.COLON, result);
    }

    private DoubleCacheUtil() {
    }
}
