package com.zopsen.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.RandomAccessFile;

public class FileClientHandler extends ChannelInboundHandlerAdapter {
    private String file_dir = "D:";
    private int dataLength = 1024;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("client read");
        if (msg instanceof EchoFile) {
            EchoFile ef = (EchoFile) msg;
            int SumCountPackage = ef.getSumCountPackage();
            int countPackage = ef.getCountPackage();
            byte[] bytes = ef.getBytes();
            String md5 = ef.getFile_md5();// 文件名

            String path = file_dir + File.separator + md5;
            File file = new File(path);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(countPackage * dataLength - dataLength);
            randomAccessFile.write(bytes);
            System.out.println("总包数：" + ef.getSumCountPackage());
            System.out.println("收到第" + countPackage + "包");
            System.out.println("本包字节数:" + bytes.length);
            countPackage = countPackage + 1;

            if (countPackage <= SumCountPackage) {
                ef.setCountPackage(countPackage);
                ctx.writeAndFlush(ef);
                randomAccessFile.close();
            } else {
                randomAccessFile.close();
                ctx.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
